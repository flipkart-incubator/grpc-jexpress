# grpc-jexpress
Developer friendly container for writing gRPC services using grpc-java, called GJEX - for Grpc Java Express.

Provides following features:
* Transparent gRPC runtime startup alongwith Jetty for dashboard/administration
* Guice module support to integrate gRPC service implementations with the gRPC runtime
* Metrics support - e.g. @Timed annotations to publish to JMX
* YAML based configuration support for gRPC service implementations
* Component Lifecycle (Start(), Stop()) support via Service interface
* Health Check - ability to add any number of deep Health Checks
* Custom web resources for enabling Control Path, Administration actions
* Filters - ability to add any number of Filters to gRPC stub method implementations
* Validation - using [Hibernate Validator](http://hibernate.org/validator/)
* Distributed Tracing - using [opentracing](http://opentracing.io/) and the [openzipkin](https://github.com/openzipkin/brave) implementation
* Concurrent execution, Circuit breaking using [Hystrix](https://github.com/Netflix/Hystrix) and Dispatch-Compose through a [FutureDecorator](https://github.com/flipkart-incubator/grpc-jexpress/blob/master/core/src/main/java/com/flipkart/gjex/core/task/FutureDecorator.java) API
* Deadlining for APIs - ability to specify execution timeouts for gRPC stubs at service end
* Task/Upstream request retries using [Hedged Requests as described here](https://cseweb.ucsd.edu/~gmporter/classes/fa17/cse124/post/schedule/p74-dean.pdf)
* Tool recommendations for testing


## Distribution
GJEX builds are distributed via the [Clojars](https://clojars.org/) community maintained repository for open source libraries.
Add the following repository to your build system to access releases builds - e.g for Maven :

```xml
<repository>
    <id>clojars</id>
    <name>Clojars repository</name>
    <url>https://clojars.org/repo</url>
</repository>
```

### Releases

Release versions are documented on the [releases page](https://github.com/flipkart-incubator/grpc-jexpress/releases). You can also refer to [clojars page](https://clojars.org/com.flipkart/grpc-jexpress/versions) for the release versions 

[![Clojars Project](https://img.shields.io/clojars/v/com.flipkart/grpc-jexpress.svg?include_prereleases)](https://clojars.org/com.flipkart/grpc-jexpress)  [![Clojars Project](https://img.shields.io/clojars/v/com.flipkart/grpc-jexpress.svg)](https://clojars.org/com.flipkart/grpc-jexpress)

More details on [Distribution Binaries](https://github.com/flipkart-incubator/grpc-jexpress/wiki/Distribution-Binaries)


## Building

You may also build GJEX from source. To build, clone this repository and run:

```
$ git clone https://github.com/flipkart-incubator/grpc-jexpress.git
$ cd grpc-jexpress
$ ./gradlew clean build publishToMavenLocal
```

## Examples


To run the hello world example with GJEX extensions, run:

```
$ ./gradlew runHelloWorldServer
```
The gRPC Server, hosted gRPC services and the Jetty server status will be displayed in the console. By attaching an MBeans explorer like JConsole, one can inspect method-level execution metrics for the gRPC services.

And in a different terminal window run:

```
$ ./gradlew runHelloWorldClient
```

## Documentation
Refer to the [wiki](https://github.com/flipkart-incubator/grpc-jexpress/wiki) for documentation.
