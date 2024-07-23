package com.flipkart.grpc.jexpress.service;

import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.grpc.jexpress.*;
import com.flipkart.grpc.jexpress.filter.CreateLoggingFilter;
import com.flipkart.grpc.jexpress.filter.GetLoggingFilter;
import io.grpc.stub.StreamObserver;
import org.apache.commons.configuration.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Named("SampleService")
public class SampleService extends UserServiceGrpc.UserServiceImplBase implements Logging {

    private ConcurrentHashMap<Integer, String> userIdToUserNameMap = new ConcurrentHashMap<>();
    private AtomicInteger lastId = new AtomicInteger(0);

    private final SampleConfiguration sampleConfiguration;
    private final Configuration flattenedConfig;
    private final Map mapConfig;
    private final String driverClass;
    private final boolean hibernateGenerateEventLog;

    @Inject
    public SampleService(SampleConfiguration sampleConfiguration,
                         @Named("GlobalFlattenedConfig") Configuration flattenedConfig,
                         @Named("GlobalMapConfig") Map mapConfig,
                         @Named("database.driverClass") String driverClass,
                         @Named("database.properties.hibernate.session.events.log") boolean hibernateGenerateEventLog)
    {
        this.sampleConfiguration = sampleConfiguration;
        this.flattenedConfig = flattenedConfig;
        this.mapConfig = mapConfig;
        this.driverClass = driverClass;
        this.hibernateGenerateEventLog = hibernateGenerateEventLog;
    }

    @Override
    @MethodFilters({GetLoggingFilter.class})
    public void getUser(GetRequest request, StreamObserver<GetResponse> responseObserver) {
        GetResponse response = GetResponse.newBuilder()
                .setId(request.getId())
                .setUserName(userIdToUserNameMap.getOrDefault(request.getId(), "Guest")).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        info(sampleConfiguration.toString());
        info(mapConfig.toString());

        info("\"database.driverClass\" class in @Named annotation =  " + driverClass);
        info("\"database.properties.hibernate.session.events.log\" in @Named annotation =  " + hibernateGenerateEventLog);

        // Read values from Flattened config
        info("FlattenedConfig has \"Grpc.server.port\" = " + flattenedConfig.getInt("Grpc.server.port"));
        info("FlattenedConfig has \"database.properties.hibernate.session.events.log\" = " + flattenedConfig.getBoolean("database.properties.hibernate.session.events.log"));
        info("FlattenedConfig has \"database.initialSize\" = " + flattenedConfig.getInt("database.initialSize"));

        // Read values from plain map
        info("MapConfig of Dashboard = " + mapConfig.get("Dashboard").toString());
        info("MapConfig of employee = " + mapConfig.get("database").toString());
        Object properties = ((Map<String, Object>) mapConfig.get("database")).get("properties");
        info("MapConfig -> properties in database = " + properties);
    }

    @Override
    @MethodFilters({CreateLoggingFilter.class})
    public void createUser(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
        int id = lastId.incrementAndGet();
        userIdToUserNameMap.put(id, request.getUserName());
        CreateResponse response = CreateResponse.newBuilder()
                .setId(id)
                .setIsCreated(true).
                        build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
