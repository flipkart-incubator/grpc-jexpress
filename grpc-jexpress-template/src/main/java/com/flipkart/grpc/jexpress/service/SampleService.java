package com.flipkart.grpc.jexpress.service;

import com.flipkart.gjex.core.filter.MethodFilters;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.grpc.jexpress.CreateRequest;
import com.flipkart.grpc.jexpress.CreateResponse;
import com.flipkart.grpc.jexpress.GetRequest;
import com.flipkart.grpc.jexpress.GetResponse;
import com.flipkart.grpc.jexpress.UserServiceGrpc;
import com.flipkart.grpc.jexpress.filter.CreateLoggingFilter;
import com.flipkart.grpc.jexpress.filter.GetLoggingFilter;
import io.grpc.stub.StreamObserver;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Named("SampleService")
public class SampleService extends UserServiceGrpc.UserServiceImplBase implements Logging {

    private ConcurrentHashMap<Integer, String> userIdToUserNameMap = new ConcurrentHashMap<>();
    private AtomicInteger lastId = new AtomicInteger(0);

    @Inject
    public SampleService() {

    }

    @Override
    @MethodFilters({GetLoggingFilter.class})
    public void getUser(GetRequest request, StreamObserver<GetResponse> responseObserver) {
        GetResponse response = GetResponse.newBuilder()
                .setId(request.getId())
                .setUserName(userIdToUserNameMap.getOrDefault(request.getId(), "Guest")).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
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
