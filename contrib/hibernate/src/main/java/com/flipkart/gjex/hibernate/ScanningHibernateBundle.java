package com.flipkart.gjex.hibernate;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.server.internal.scanning.AnnotationAcceptingListener;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import javax.persistence.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public abstract class ScanningHibernateBundle <T extends GJEXConfiguration , U extends Map> extends HibernateBundle<T,U> {

    protected ScanningHibernateBundle(String pckg) {
        this(pckg, new SessionFactoryFactory());
    }

    protected ScanningHibernateBundle(String pckg, SessionFactoryFactory sessionFactoryFactory) {
        this(new String[]{pckg}, sessionFactoryFactory);
    }

    protected ScanningHibernateBundle(String[] pckgs, SessionFactoryFactory sessionFactoryFactory) {
        super(findEntityClassesFromDirectory(pckgs), sessionFactoryFactory);
    }

    public static ImmutableList<Class<?>> findEntityClassesFromDirectory(String[] pckgs) {
        AnnotationAcceptingListener asl = new AnnotationAcceptingListener(new Class[]{Entity.class});
        PackageNamesScanner scanner = new PackageNamesScanner(pckgs, true);

        while (scanner.hasNext()) {
            String next = scanner.next();
            if (asl.accept(next)) {
                try {
                    InputStream in = scanner.open();
                    Throwable var5 = null;

                    try {
                        asl.process(next, in);
                    } catch (Throwable var15) {
                        var5 = var15;
                        throw var15;
                    } finally {
                        if (in != null) {
                            if (var5 != null) {
                                try {
                                    in.close();
                                } catch (Throwable var14) {
                                    var5.addSuppressed(var14);
                                }
                            } else {
                                in.close();
                            }
                        }

                    }
                } catch (IOException var17) {
                    throw new RuntimeException("AnnotationAcceptingListener failed to process scanned resource: " + next);
                }
            }
        }

        ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();
        Iterator var19 = asl.getAnnotatedClasses().iterator();

        while (var19.hasNext()) {
            Class<?> clazz = (Class) var19.next();
            builder.add(clazz);
        }

        return builder.build();
    }
}
