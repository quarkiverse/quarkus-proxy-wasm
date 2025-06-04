package io.quarkiverse.proxywasm.deployment;

import java.util.List;

import org.jboss.jandex.DotName;

import io.quarkiverse.proxywasm.runtime.VertxHttpRequestAdaptor;
import io.quarkiverse.proxywasm.runtime.VertxServerAdaptor;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.jaxrs.spi.deployment.AdditionalJaxRsResourceMethodAnnotationsBuildItem;
import io.roastedroot.proxywasm.jaxrs.ProxyWasm;
import io.roastedroot.proxywasm.jaxrs.cdi.ProxyWasmFeature;

/**
 * Quarkus build-time processor for the Proxy-Wasm extension.
 * This processor configures the necessary components for the Proxy-Wasm integration
 * including CDI beans, JAX-RS annotations, and feature registration.
 */
public class ProxyWasmProcessor {

    private static final String FEATURE = "proxy-wasm";

    /**
     * Registers the proxy-wasm feature with Quarkus.
     *
     * @return A FeatureBuildItem representing the proxy-wasm feature.
     */
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Registers additional CDI beans required for Proxy-Wasm functionality.
     *
     * @return An AdditionalBeanBuildItem containing the required beans.
     */
    @BuildStep
    AdditionalBeanBuildItem resources() {
        return new AdditionalBeanBuildItem(
                ProxyWasmFeature.class, VertxServerAdaptor.class, VertxHttpRequestAdaptor.class);
    }

    /**
     * Registers the ProxyWasm annotation as a JAX-RS resource method annotation.
     *
     * @return An AdditionalJaxRsResourceMethodAnnotationsBuildItem containing the ProxyWasm annotation.
     */
    @BuildStep
    public AdditionalJaxRsResourceMethodAnnotationsBuildItem annotations() {
        return new AdditionalJaxRsResourceMethodAnnotationsBuildItem(
                List.of(DotName.createSimple(ProxyWasm.class.getName())));
    }
}
