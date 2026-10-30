package io.microsphere.spring.cloud.openfeign.components;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * The no-op {@link RequestInterceptor} that performs no operation on the {@link RequestTemplate},
 * typically used as a placeholder or default interceptor.
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * RequestInterceptor interceptor = NoOpRequestInterceptor.INSTANCE;
 * interceptor.apply(requestTemplate); // does nothing
 * }</pre>
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see RequestInterceptor
 * @see CompositedRequestInterceptor
 * @since 1.0.0
 */
public class NoOpRequestInterceptor implements RequestInterceptor {

    /**
     * The singleton instance of {@link NoOpRequestInterceptor}
     */
    public static final NoOpRequestInterceptor INSTANCE = new NoOpRequestInterceptor();

    /**
     * Does nothing on the given {@link RequestTemplate}.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * NoOpRequestInterceptor.INSTANCE.apply(requestTemplate); // no-op
     * }</pre>
     *
     * @param template the {@link RequestTemplate} to be ignored
     */
    @Override
    public void apply(RequestTemplate template) {
        //no op
    }
}