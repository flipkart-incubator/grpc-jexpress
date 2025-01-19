package com.flipkart.gjex.hibernate;

import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.hibernate.SessionFactory;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Provider
public class UnitOfWorkApplicationListener implements ApplicationEventListener {
    private Map<Method, UnitOfWork> methodMap = new HashMap();
    private Map<String, SessionFactory> sessionFactories = new HashMap();

    public UnitOfWorkApplicationListener() {
    }

    public UnitOfWorkApplicationListener(String name, SessionFactory sessionFactory) {
        this.registerSessionFactory(name, sessionFactory);
    }

    public void registerSessionFactory(String name, SessionFactory sessionFactory) {
        this.sessionFactories.put(name, sessionFactory);
    }

    public void onEvent(ApplicationEvent event) {
        if (event.getType() == ApplicationEvent.Type.INITIALIZATION_APP_FINISHED) {
            Iterator var2 = event.getResourceModel().getResources().iterator();

            while (var2.hasNext()) {
                Resource resource = (Resource) var2.next();
                Iterator var4 = resource.getAllMethods().iterator();

                while (var4.hasNext()) {
                    ResourceMethod method = (ResourceMethod) var4.next();
                    this.registerUnitOfWorkAnnotations(method);
                }

                var4 = resource.getChildResources().iterator();

                while (var4.hasNext()) {
                    Resource childResource = (Resource) var4.next();
                    Iterator var6 = childResource.getAllMethods().iterator();

                    while (var6.hasNext()) {
                        ResourceMethod method = (ResourceMethod) var6.next();
                        this.registerUnitOfWorkAnnotations(method);
                    }
                }
            }
        }

    }

    public RequestEventListener onRequest(RequestEvent event) {
        return new UnitOfWorkEventListener(this.methodMap, this.sessionFactories);
    }

    private void registerUnitOfWorkAnnotations(ResourceMethod method) {
        UnitOfWork annotation = (UnitOfWork) method.getInvocable().getDefinitionMethod().getAnnotation(UnitOfWork.class);
        if (annotation == null) {
            annotation = (UnitOfWork) method.getInvocable().getHandlingMethod().getAnnotation(UnitOfWork.class);
        }

        if (annotation != null) {
            this.methodMap.put(method.getInvocable().getDefinitionMethod(), annotation);
        }

    }

    private static class UnitOfWorkEventListener implements RequestEventListener {
        private final Map<Method, UnitOfWork> methodMap;
        private final UnitOfWorkAspect unitOfWorkAspect;

        UnitOfWorkEventListener(Map<Method, UnitOfWork> methodMap, Map<String, SessionFactory> sessionFactories) {
            this.methodMap = methodMap;
            this.unitOfWorkAspect = new UnitOfWorkAspect(sessionFactories);
        }

        public void onEvent(RequestEvent event) {
            RequestEvent.Type eventType = event.getType();
            if (eventType == RequestEvent.Type.RESOURCE_METHOD_START) {
                UnitOfWork unitOfWork = (UnitOfWork) this.methodMap.get(event.getUriInfo().getMatchedResourceMethod().getInvocable().getDefinitionMethod());
                this.unitOfWorkAspect.beforeStart(unitOfWork);
            } else if (eventType == RequestEvent.Type.RESP_FILTERS_START) {
                try {
                    this.unitOfWorkAspect.afterEnd();
                } catch (Exception var4) {
                    throw new MappableException(var4);
                }
            } else if (eventType == RequestEvent.Type.ON_EXCEPTION) {
                this.unitOfWorkAspect.onError();
            } else if (eventType == RequestEvent.Type.FINISHED) {
                this.unitOfWorkAspect.onFinish();
            }

        }
    }
}
