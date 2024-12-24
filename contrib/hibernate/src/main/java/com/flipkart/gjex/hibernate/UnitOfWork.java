package com.flipkart.gjex.hibernate;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnitOfWork {
    boolean readOnly() default false;

    boolean transactional() default true;

    CacheMode cacheMode() default CacheMode.NORMAL;

    FlushMode flushMode() default FlushMode.AUTO;

    String value() default "hibernate";
}

