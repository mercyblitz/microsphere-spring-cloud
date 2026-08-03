package io.microsphere.spring.cloud.openfeign.encoder;

import feign.InvocationHandlerFactory;
import feign.codec.Encoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedEncoder;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class EncoderComponentAssert extends FeignComponentAssert<Encoder> {

    public static final EncoderComponentAssert INSTANCE = new EncoderComponentAssert();

    private EncoderComponentAssert() {
    }

    @Override
    protected Encoder loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        Object buildTemplateFromArgsValue = getFieldValue(true, methodHandler, "buildTemplateFromArgs");
        DecoratedEncoder encoder = getFieldValue(true, buildTemplateFromArgsValue, "encoder");
        return encoder.delegate();
    }
}