package com.hvisions.iot.core.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Driver {

    String[] names();
    boolean needNewExecutor() default false;

    Class<?> driverConfig() default BaseConnectionConfig.class;
}