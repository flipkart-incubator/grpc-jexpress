package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.grpc.channel.GrpcChannelConfig;
import com.flipkart.gjex.grpc.channel.InstrumentedChannel;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import io.grpc.Channel;



import java.lang.reflect.InvocationTargetException;

/**
 * Created by rohit.k on 28/07/18.
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

      }

      @Override
      public T get() {
          try {
              return clazz.getConstructor(channel.getClass()).newInstance();
          } catch (InstantiationException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
              throw new RuntimeException("Grpc stub class doesn't have a constructor which only takes  'Channel' as parameter", e);
          }
      }
  }
}
