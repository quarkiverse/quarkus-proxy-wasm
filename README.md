# Quarkus Proxy-WASM Extension
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.proxy-wasm/quarkus-proxy-wasm?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.proxy-wasm/quarkus-proxy-wasm-parent)

The Java implementation for proxy-wasm, enabling developer to run Proxy-Wasm plugins in Java.

## Overview

Proxy-Wasm is a plugin system for network proxies. It lets you write plugins that can act as request filters in a
portable, sandboxed, and language-agnostic way, thanks to WebAssembly.

This Quarkus extension allows you to use Proxy-Wasm plugins to filter requests to Jakarta REST (formerly known as JAX-RS)
endpoints.

Adding a Proxy-Wasm plugin to a JAX-RS for a "waf" proxy-wasm module is as simple as adding a `@ProxyWasm` annotation 
to a method or class:

```java
package org.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.roastedroot.proxywasm.jaxrs.ProxyWasm;

@Path("/example")
public class Example {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @ProxyWasm("waf")
    public String hello() {
        return "hello";
    }
}
```

And then using the [`Plugin builder`](https://javadoc.io/doc/io.roastedroot/proxy-wasm-java-host/latest/io/roastedroot/proxywasm/Plugin.Builder.html) API to configure the Proxy-Wasm plugin that has a matching name:

```java
package org.example;

import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import io.roastedroot.proxywasm.StartException;
import io.roastedroot.proxywasm.Plugin;
import io.roastedroot.proxywasm.PluginFactory;
import io.roastedroot.proxywasm.SimpleMetricsHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class App {

    private static WasmModule module =
        Parser.parse(App.class.getResourceAsStream("coraza-proxy-wasm.wasm"));

    @Produces
    public PluginFactory waf() throws StartException {
        return () ->
                Plugin.builder(module)
                        .withName("waf")
                        .withPluginConfig(" ... the config passed to the plugin ... ")
                        .withMetricsHandler(new SimpleMetricsHandler())
                        .build();
    }
}
```

### Compiling WASM to Bytecode (Experimental)

By default, wsm modules are executed using the [Chicory](https://chicory.dev/) interpreter.  But if you want the wasm to
run a near native speed, you should compile the WASM to Java bytecode using the chicory WASM to bytecode compiler.
Chicory supports compiling the WASM module at either build time or runtime.  If you want to compile your Quarkus app to 
native,  then you MUST compile the WASM module at build (time to avoid the use of runtime reflection).  Please refer
to the [Chicory documentation](https://chicory.dev/docs/) for more details on how to compile the WASM module to Java 
bytecode.  Compiling will produce a machine factory that you should pass as an argument to the [withMachineFactory](https://javadoc.io/doc/io.roastedroot/proxy-wasm-java-host/latest/io/roastedroot/proxywasm/Plugin.Builder.html#withMachineFactory(java.util.function.Function)) 
method to enable the bytecode execution of the WASM module.

## Docs

* [Usage Guide](./docs/modules/ROOT/pages/index.adoc)
* [Proxy-Wasm JavaDocs](https://javadoc.io/doc/io.roastedroot/proxy-wasm-java-host/latest/io/roastedroot/proxywasm/package-summary.html)

### Docs and SDKs for plugin authors:

* [ABI specification](https://github.com/istio-ecosystem/wasm-extensions[Proxy-Wasm)
* [C++ SDK](https://github.com/proxy-wasm/proxy-wasm-cpp-sdk[Proxy-Wasm)
* [Rust SDK](https://github.com/proxy-wasm/proxy-wasm-rust-sdk[Proxy-Wasm)
* [Go SDK](https://github.com/proxy-wasm/proxy-wasm-go-sdk[Proxy-Wasm)
* [AssemblyScript SDK](https://github.com/solo-io/proxy-runtime[Proxy-Wasm)

### Popular Proxy-Wasm plugins:

* [Coraza WAF](link:https://github.com/corazawaf/coraza-proxy-wasm)
* [Kuadrant](link:https://github.com/Kuadrant/wasm-shim/)


## Building

To build the project, you need to have Maven installed. You can build the project using the following command:

```bash
mvn clean install
```

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="http://hiramchirino.com"><img src="https://avatars.githubusercontent.com/u/103255?v=4?s=100" width="100px;" alt="Hiram Chirino"/><br /><sub><b>Hiram Chirino</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-proxy-wasm/commits?author=chirino" title="Code">💻</a> <a href="#maintenance-chirino" title="Maintenance">🚧</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!