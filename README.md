# Quarkus Proxy-WASM Extension
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.proxy-wasm/quarkus-proxy-wasm?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.proxy-wasm/quarkus-proxy-wasm-parent)
[![Javadocs](http://javadoc.io/badge/io.quarkiverse.proxy-wasm/quarkus-proxy-wasm.svg)](http://javadoc.io/doc/io.quarkiverse.proxy-wasm/quarkus-proxy-wasm)

The Quarkus implementation for proxy-wasm, enabling developer to run Proxy-Wasm plugins in Java.

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

And then using the [`PluginFactory builder`](https://javadoc.io/doc/io.roastedroot/proxy-wasm-java-host/latest/io/roastedroot/proxywasm/PluginFactory.Builder.html) API to configure the Proxy-Wasm plugin that has a matching name:

```java
package org.example;

import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;
import io.roastedroot.proxywasm.PluginFactory;
import io.roastedroot.proxywasm.SimpleMetricsHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class App {

    private static WasmModule module =
        Parser.parse(App.class.getResourceAsStream("coraza-proxy-wasm.wasm"));

    @Produces
    public PluginFactory waf() {
        return PluginFactory.builder(module)
                        .withName("waf")
                        .withPluginConfig(" ... the config passed to the plugin ... ")
                        .withMetricsHandler(new SimpleMetricsHandler())
                        .build();
    }
}
```

### Compiling WASM to Bytecode

By default, WASM modules are executed using the [Chicory](https://chicory.dev/) interpreter.  But if you want the WASM code to
run a near native speed, you should compile the WASM to Java bytecode using the chicory WASM to bytecode compiler.
Chicory supports compiling the WASM module at either build time or runtime.  

#### Runtime Compilation

To enable runtime compilation, you need just need to add the following dependency to your `pom.xml`:

```xml  
<dependency>
  <groupId>com.dylibso.chicory</groupId>
  <artifactId>compiler</artifactId>
</dependency>
```

You then enable it on the PluginFactory builder by using it as the machine factory:

```java
@Produces
public PluginFactory waf() {
    return PluginFactory.builder(module)
                    .withMachineFactory(MachineFactoryCompiler::compile)
                    .withName("waf")
                    .withPluginConfig(CONFIG)
                    .withMetricsHandler(new SimpleMetricsHandler())
                    .build();
}
```

Please refer to the [Chicory Runtime Compilation documentation](https://chicory.dev/docs/usage/runtime-compiler)
for more details.

#### Build time Compilation

If you want to compile your Quarkus app to native,  then you MUST compile the WASM module at build time to avoid the use 
of runtime reflection.  

To compile your WASM module at build time, add the Chicory compiler Maven plugin to your `pom.xml`:

```xml
<plugin>
    <groupId>com.dylibso.chicory</groupId>
    <artifactId>chicory-compiler-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>chicory-compile</id>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <name>org.example.internal.WasmShim</name>
                <wasm>src/main/resources/plugin.wasm</wasm>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will generate a `WasmShim` class that provides both a `load()` method to get the `WasmModule` and a `create()` 
method for the machine factory. Update your plugin factory to use the compiled module:

```java
@Produces
public PluginFactory waf() {
    return PluginFactory.builder(WasmShim.load())
                    .withMachineFactory(WasmShim::create)
                    .withName("waf")
                    .withPluginConfig(CONFIG)
                    .withMetricsHandler(new SimpleMetricsHandler())
                    .build();
}
```

Please refer to the [Chicory Build time Compilation documentation](https://chicory.dev/docs/usage/build-time-compiler)
for more details.

## Docs

* [Usage Guide](./docs/modules/ROOT/pages/index.adoc)
* [Proxy-Wasm JavaDocs](https://javadoc.io/doc/io.roastedroot/proxy-wasm-java-host/latest/io/roastedroot/proxywasm/package-summary.html)

## Examples

* [Coraza Example](integration-tests/corazawaf-example) - Quarkus app that uses the Coraza WAF Proxy-Wasm plugin to filter requests.
* [Kuadrant Example](integration-tests/kuadrant-example) - Quarkus app that uses the Kuadrant Proxy-Wasm plugin to filter requests.

### Docs and SDKs for plugin authors:

* [ABI specification](https://github.com/istio-ecosystem/wasm-extensions[Proxy-Wasm)
* [C++ SDK](https://github.com/proxy-wasm/proxy-wasm-cpp-sdk[Proxy-Wasm)
* [Rust SDK](https://github.com/proxy-wasm/proxy-wasm-rust-sdk[Proxy-Wasm)
* [Go SDK](https://github.com/proxy-wasm/proxy-wasm-go-sdk[Proxy-Wasm)
* [AssemblyScript SDK](https://github.com/solo-io/proxy-runtime[Proxy-Wasm)

### Popular Proxy-Wasm plugins:

* [Coraza WAF](https://github.com/corazawaf/coraza-proxy-wasm)
* [Kuadrant](https://github.com/Kuadrant/wasm-shim/)
* [Higress](https://higress.cn/en/plugin/)

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