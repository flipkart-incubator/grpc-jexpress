package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.grpc.channel.GrpcChannelConfig;
import com.flipkart.gjex.grpc.channel.InstrumentedChannel;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import io.grpc.Channel;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by rohit.k on 28/07/18.
 */

/**
 * {@link ClientModule} is a guice module to get an Instrumented instance of GRPC client
 * @param <T> type of the required Grpc Service's stub eg: GreeterGrpc.GreeterBlockingStub
 */
public class ClientModule<T> extends AbstractModule{
    private final Class<T> clazz;
    private final GrpcChannelConfig channelConfig;

    public ClientModule(Class<T> clazz, GrpcChannelConfig channelConfig){
        this.clazz = clazz;
        this.channelConfig = channelConfig;
    }

    @Override
    protected void configure(){
        bind(clazz).toProvider(new StubProvider());
    }

  private class StubProvider implements Provider<T>{
      private Channel channel;

      public  StubProvider(){
          channel = new InstrumentedChannel(channelConfig);
          binder().requestInjection(channel);
      }

      @Override
      public T get() {
          try {
              Constructor<T> constructor = clazz.getDeclaredConstructor(Channel.class);
              constructor.setAccessible(true);
              return constructor.newInstance(channel);
          } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
              throw new RuntimeException("Grpc stub class doesn't have a constructor which only takes  'Channel' as parameter", e);
          }
      }
  }

    public static void main(String[] args) {

    }
}
