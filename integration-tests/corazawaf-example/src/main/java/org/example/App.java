package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.example.internal.CorazaWAF;

import io.roastedroot.proxywasm.LogHandler;
import io.roastedroot.proxywasm.PluginFactory;
import io.roastedroot.proxywasm.SimpleMetricsHandler;

/**
 * Application configuration class for the Coraza WAF example.
 * Sets up the Wasm PluginFactory for the WAF plugin.
 */
@ApplicationScoped
public class App {

    /**
     * Default constructor.
     */
    public App() {
        // Default constructor
    }

    static final String CONFIG;

    static {
        try (InputStream is = App.class.getResourceAsStream("waf-config.json")) {
            CONFIG = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static final boolean DEBUG = "true".equals(System.getenv("DEBUG"));

    /**
     * Produces the PluginFactory for the Coraza WAF Wasm plugin.
     * Configures the plugin with necessary settings like name, shared status,
     * logger, plugin configuration, and metrics handler.
     *
     * @return A configured PluginFactory for the WAF plugin.
     */
    @Produces
    public PluginFactory waf() {
        return PluginFactory.builder(CorazaWAF.load())
                .withMachineFactory(CorazaWAF::create)
                .withName("waf")
                .withShared(true)
                .withLogger(DEBUG ? LogHandler.SYSTEM : null)
                .withPluginConfig(CONFIG)
                .withMetricsHandler(new SimpleMetricsHandler())
                .build();
    }
}
