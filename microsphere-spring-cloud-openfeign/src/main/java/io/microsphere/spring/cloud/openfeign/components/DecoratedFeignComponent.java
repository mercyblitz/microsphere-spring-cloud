package io.microsphere.spring.cloud.openfeign.components;

import io.microsphere.annotation.Nonnull;
import io.microsphere.logging.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.reflect.ConstructorUtils.findConstructor;
import static org.springframework.beans.BeanUtils.instantiateClass;

/**
 * The abstract decorated class for the Feign components that delegates to the original component
 * and supports the runtime refresh of the delegate when the {@link FeignClientProperties} changed.
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * DecoratedRetryer decoratedRetryer = DecoratedFeignComponent.instantiate(
 *     DecoratedRetryer.class, Retryer.class, contextId, feignContext, clientProperties, retryer);
 * }</pre>
 *
 * @param <T> the type of the decorated Feign component
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Refreshable
 * @see AutoRefreshCapability
 * @since 1.0.0
 */
public abstract class DecoratedFeignComponent<T> implements Refreshable {

    /**
     * The {@link Logger} for the subclass instances
     */
    protected final Logger logger = getLogger(getClass());

    private final FeignContext feignContext;

    private final String contextId;

    private final FeignClientProperties clientProperties;

    /**
     * The delegate instance of the Feign component, may be {@code null} after {@link #refresh()}
     * until the next call of {@link #delegate()}
     */
    protected volatile T delegate;

    /**
     * Constructs a new {@link DecoratedFeignComponent} wrapping the given delegate.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Typically invoked via a subclass constructor:
     * DecoratedDecoder decoder = new DecoratedDecoder(contextId, feignContext, clientProperties, originalDecoder);
     * }</pre>
     *
     * @param contextId        the Feign client context identifier
     * @param feignContext     the {@link FeignContext} for loading component instances
     * @param clientProperties the {@link FeignClientProperties} containing configuration
     * @param delegate         the original Feign component to decorate
     */
    public DecoratedFeignComponent(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, T delegate) {
        this.contextId = contextId;
        this.feignContext = feignContext;
        this.clientProperties = clientProperties;
        this.delegate = delegate;
    }

    /**
     * Returns the current delegate instance. If the delegate has been cleared (e.g., after
     * a refresh), a new instance is loaded from the {@link FeignContext}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Decoder actual = decoratedDecoder.delegate();
     * }</pre>
     *
     * @return the delegate Feign component instance, never {@code null}
     */
    public T delegate() {
        T delegate = this.delegate;
        if (delegate == null) {
            delegate = loadInstance();
            logger.trace("the component[{}] - Creating delegate instance[{}] for contextId: '{}'", componentType(), delegate, contextId);
            this.delegate = delegate;
        }
        return delegate;
    }

    /**
     * Loads a component instance of the given type from the {@link NamedContextFactory},
     * falling back to direct instantiation if the bean is not available.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Decoder decoder = decoratedFeignComponent.loadInstanceFromContextFactory("my-client", Decoder.class);
     * }</pre>
     *
     * @param <T>           the component type
     * @param contextId     the Feign client context ID
     * @param componentType the class of the component to load
     * @return the loaded component instance
     */
    @Nonnull
    public <T> T loadInstanceFromContextFactory(String contextId, Class<T> componentType) {
        ObjectProvider<T> beanProvider = this.feignContext.getProvider(contextId, componentType);
        return beanProvider.getIfAvailable(() -> instantiateClass(componentType));
    }

    /**
     * Returns the Feign client context identifier associated with this component.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * String id = decoratedComponent.contextId();
     * }</pre>
     *
     * @return the context identifier, never {@code null}
     */
    @Nonnull
    public String contextId() {
        return this.contextId;
    }

    /**
     * Refreshes this component by clearing the current delegate. The next call to
     * {@link #delegate()} will reload a fresh instance from the {@link FeignContext}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * decoratedComponent.refresh();
     * }</pre>
     */
    public void refresh() {
        if (logger.isTraceEnabled()) {
            logger.trace("the component[{}] - Refreshing delegate instance[{}] for contextId : '{}'", componentType(), this.delegate, contextId);
        }
        this.delegate = null;
    }

    /**
     * Returns the Feign component type class used to resolve the delegate implementation.
     * Subclasses must implement this to return the appropriate configuration class.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Class<? extends T> type = decoratedFeignComponent.componentType();
     * }</pre>
     *
     * @return the component type class
     */
    protected abstract Class<? extends T> componentType();

    /**
     * Returns the default {@link FeignClientConfiguration} for this Feign client,
     * as specified by the default config name in {@link FeignClientProperties}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * FeignClientConfiguration defaultConfig = decoratedComponent.getDefaultConfiguration();
     * }</pre>
     *
     * @return the default {@link FeignClientConfiguration}, or {@code null} if not configured
     */
    public FeignClientConfiguration getDefaultConfiguration() {
        return this.clientProperties.getConfig().get(this.clientProperties.getDefaultConfig());
    }

    /**
     * Returns the {@link FeignClientConfiguration} for the current Feign client context.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * FeignClientConfiguration currentConfig = decoratedComponent.getCurrentConfiguration();
     * }</pre>
     *
     * @return the current {@link FeignClientConfiguration}, or {@code null} if not configured
     */
    public FeignClientConfiguration getCurrentConfiguration() {
        return this.clientProperties.getConfig().get(contextId);
    }

    /**
     * Resolves the configuration value by applying the given {@link Function} to the
     * {@link #getDefaultConfiguration() default} {@link FeignClientConfiguration} first,
     * then falling back to the {@link #getCurrentConfiguration() current} one.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Class<Decoder> decoderClass = get(FeignClientConfiguration::getDecoder);
     * }</pre>
     *
     * @param configurationFunction the {@link Function} to extract the value from the {@link FeignClientConfiguration}
     * @param <T>                   the type of the resolved value
     * @return the resolved value, or {@code null} if not found in both configurations
     */
    protected <T> T get(Function<FeignClientConfiguration, T> configurationFunction) {
        FeignClientConfiguration config = getDefaultConfiguration();
        T value = null;
        if (config != null) {
            value = configurationFunction.apply(config);
        }
        if (value == null) {
            config = getCurrentConfiguration();
            if (config != null) {
                value = configurationFunction.apply(config);
            }
        }
        return value;
    }

    /**
     * Loads a fresh instance of the Feign component from the {@link FeignContext} using
     * the {@link #componentType()} and {@link #contextId()}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * T freshInstance = decoratedComponent.loadInstance();
     * }</pre>
     *
     * @return a new instance of the Feign component
     */
    protected T loadInstance() {
        Class<? extends T> componentType = componentType();
        String contextId = contextId();
        return loadInstanceFromContextFactory(contextId, componentType);
    }

    /**
     * Returns the hash code of the delegate component.
     *
     * @return the delegate's hash code
     */
    @Override
    public int hashCode() {
        return delegate().hashCode();
    }

    /**
     * Delegates equality check to the underlying delegate component.
     *
     * @param obj the object to compare with
     * @return {@code true} if the delegate considers the objects equal
     */
    @Override
    public boolean equals(Object obj) {
        return delegate().equals(obj);
    }

    /**
     * Returns the string representation of the delegate component.
     *
     * @return the delegate's string representation
     */
    @Override
    public String toString() {
        return delegate().toString();
    }

    /**
     * Instantiates the given decorated Feign component class via its constructor accepting
     * ({@code String}, {@link FeignContext}, {@link FeignClientProperties}, component instance).
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * DecoratedRetryer decoratedRetryer = DecoratedFeignComponent.instantiate(
     *     DecoratedRetryer.class, Retryer.class, contextId, feignContext, clientProperties, retryer);
     * }</pre>
     *
     * @param <W>              the decorated Feign component type
     * @param <T>              the delegate Feign component type
     * @param decoratedClass   the class of the decorated Feign component to instantiate
     * @param componentClass   the class of the delegate Feign component
     * @param contextId        the Feign client context identifier
     * @param feignContext     the {@link FeignContext} for loading component instances
     * @param clientProperties the {@link FeignClientProperties} containing configuration
     * @param delegate         the original Feign component to decorate
     * @return a new instance of the decorated Feign component
     */
    public static <W extends DecoratedFeignComponent<T>, T> W instantiate(Class<W> decoratedClass, Class<T> componentClass,
                                                                          String contextId, FeignContext feignContext, FeignClientProperties clientProperties, T delegate) {
        Constructor<W> constructor = findConstructor(decoratedClass, String.class, FeignContext.class, FeignClientProperties.class, componentClass);
        return instantiateClass(constructor, contextId, feignContext, clientProperties, delegate);
    }
}