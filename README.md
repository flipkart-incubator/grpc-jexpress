# grpc-jexpress
Productivity extensions to grpc-java, called GJEX - for Grpc Java Express.

Provides following features:
* Transparent gRPC runtime startup alongwith Jetty for dashboard/administration
* Guice integration to integrate gRPC service implementations with the gRPC runtime
* Metrics support - e.g. @Timed annotations to publish to JMX
* YAML based configuration support for gRPC service implementations 

## Building
To build GJEX, clone this repository and run:

```
$ ./gradlew clean build install
```

## Examples
The [examples](https://github.com/flipkart-incubator/grpc-jexpress/tree/master/examples) requires https://github.com/grpc/grpc-java/tree/master/examples to already be built. 

To build the examples, run in the 'examples' directory:

```
$ ../gradlew installDist
```
To run the hello world example with GJEX extensions, run:

```
$ ./build/install/examples/bin/hello-world-server
```
The gRPC Server, hosted gRPC services and the Jetty server status will be displayed in the console. By attaching an MBeans explorer like JConsole, one can inspect method-level execution metrics for the gRPC services.

And in a different terminal window run:

```
$ ./build/install/examples/bin/hello-world-client
```

