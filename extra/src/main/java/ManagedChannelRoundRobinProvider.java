import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ManagedChannelRoundRobinProvider extends RoundRobinProvider<ManagedChannel>{
    private static int grpcThreadPoolSize = 128;
    private static String grpcServerIp = "";
    private static final String LOAD_BALANCING_POLICY = "round_robin";
    private int maxInboundMessageSize;

    public ManagedChannelRoundRobinProvider(String grpcServerIp, int maxCount,
                                            int grpcThreadPoolSize, int maxInboundMessageSize) {
        ManagedChannelRoundRobinProvider.grpcServerIp = grpcServerIp;
        ManagedChannelRoundRobinProvider.grpcThreadPoolSize = grpcThreadPoolSize;
        this.maxCount = maxCount;
        objects = new ManagedChannel[this.maxCount];
        this.maxInboundMessageSize = maxInboundMessageSize;
        populateObjects();
    }

    @Override
    protected ManagedChannel createObject() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(grpcThreadPoolSize);
        ManagedChannelBuilder channel = ManagedChannelBuilder
            .forTarget(grpcServerIp)
            .maxInboundMessageSize(maxInboundMessageSize)
            //.keepAliveTime(60, TimeUnit.SECONDS)
            //.keepAliveTimeout(1,TimeUnit.SECONDS)
            .defaultLoadBalancingPolicy(LOAD_BALANCING_POLICY)
            //.maxRetryAttempts(3)
            .usePlaintext()
            .idleTimeout(10, TimeUnit.MINUTES)
            .executor(forkJoinPool);
        return channel.build();
    }

    @Override
    protected void populateObjects() {
        for (int i=0;i<maxCount;i++) {
            objects[i] = createObject();
        }
    }
}
