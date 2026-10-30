package io.microsphere.spring.cloud.context.named.config;

import io.microsphere.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import io.microsphere.spring.cloud.context.named.SpecificationCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;

import java.util.List;

import static io.microsphere.spring.beans.BeanUtils.getSortedBeans;

/**
 * {@link BeanPostProcessor} for {@link Specification}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SpecificationCustomizer
 * @since 0.0.1
 */
public class SpecificationBeanPostProcessor extends GenericBeanPostProcessorAdapter<Specification> implements BeanFactoryAware {

    private List<SpecificationCustomizer> customizers;

    /**
     * {@inheritDoc}
     * <p>Customizes the {@link Specification} bean with all sorted {@link SpecificationCustomizer}
     * beans, which are resolved lazily in {@link #setBeanFactory(BeanFactory)}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Invoked automatically by the Spring container after each Specification bean initialization
     * }</pre>
     *
     * @param bean     the initialized {@link Specification} bean
     * @param beanName the name of the {@link Specification} bean
     * @throws BeansException if the customization fails
     */
    @Override
    protected void processAfterInitialization(Specification bean, String beanName) throws BeansException {
        customizers.forEach(customizer -> customizer.customize(bean, beanName));
    }

    /**
     * {@inheritDoc}
     * <p>Resolves all sorted {@link SpecificationCustomizer} beans from the given {@link BeanFactory}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Invoked automatically by the Spring container during bean initialization
     * }</pre>
     *
     * @param beanFactory the {@link BeanFactory} to resolve the {@link SpecificationCustomizer} beans
     * @throws BeansException if the {@link SpecificationCustomizer} beans can't be resolved
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.customizers = getSortedBeans(beanFactory, SpecificationCustomizer.class);
    }
}