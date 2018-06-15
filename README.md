# grpc-jexpress
Productivity extensions to grpc-java

Provides following features:
* Transparent Grpc runtime startup alongwith Jetty for dashboard/administration
* Guice integration to integrate Grpc service implementations with the Grpc runtime
* Metrics support - e.g. @Timed annotations to publish to JMX
* YAML based configuration support for Grpc service implementations 

## Examples
The [examples](https://github.com/flipkart-incubator/grpc-jexpress/tree/master/examples) requires https://github.com/grpc/grpc-java/tree/master/examples to already be built. 

To build the examples, run in the 'examples' directory:

```
$ ../gradlew installDist
```
To run the hello world example with GJEX extensions, run:

