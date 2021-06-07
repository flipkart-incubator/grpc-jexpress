## 1.36
- Bug fixes: Fixed headers being sent in RPC response
- Adding prefix to dashboard & api instrumentation 

## 1.33 (Nov 9, 2019)
- Bug fixes
- Enhanced configuration support
- Support for carrying forward gRPC headers to service implementations

## 1.0 (Jan 11, 2019)
- **New features:**
  - Transparent gRPC runtime startup alongwith Jetty for dashboard/administration
  - Guice module support to integrate gRPC service implementations with the gRPC runtime
  - Metrics support - e.g. @Timed annotations to publish to JMX
  - YAML based configuration support for gRPC service implementations
  - Component Lifecycle (Start(), Stop()) support via Service interface
  - Health Check - ability to add any number of deep Health Checks
  - Filters - ability to add any number of Filters to gRPC stub method implementations
  - Validation - using Hibernate Validator
  - Distributed Tracing - using opentracing and the openzipkin implementation
  - Concurrent execution, Circuit breaking using Hystrix and Dispatch-Compose through a FutureDecorator API
  - Deadlining for APIs - ability to specify execution timeouts for gRPC stubs at service end
  - Task/Upstream request retries using Hedged Requests 
  - Tool recommendations for testing
  
