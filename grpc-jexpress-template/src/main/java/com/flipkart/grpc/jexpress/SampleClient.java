package com.flipkart.grpc.jexpress;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class SampleClient {
    private ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public SampleClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    private SampleClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    private void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
    }


    private CreateResponse createUser(String user) {
        CreateRequest request = CreateRequest
                .newBuilder()
                .setUserName(user)
                .build();
        CreateResponse reply = null;
        try {
            reply = blockingStub.createUser(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reply;
    }

    private GetResponse getUser(int id) {
        GetRequest request = GetRequest
                .newBuilder()
                .setId(id)
                .build();
        GetResponse reply = null;
        try {
            reply = blockingStub.getUser(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reply;
    }

    public static void main(String[] args) throws InterruptedException {
        SampleClient client = new SampleClient("localhost", 50051);
        String user = "Sample";
        System.out.println("number of Arguments: " + args.length);
        if (args.length > 0) {
            user = args[0];
        }
        System.out.println("Trying to create account for:" + user);
        try {
            CreateResponse createResponse = client.createUser("Foo");
            System.out.println(createResponse);
            GetResponse getResponse = client.getUser(createResponse.getId());
            System.out.println(getResponse);

        } finally {
            client.shutdown();
        }
    }

}
