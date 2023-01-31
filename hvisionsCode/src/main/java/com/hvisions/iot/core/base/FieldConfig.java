package com.hvisions.iot.core.base;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldConfig {

    String label();

    String description() default "";

    String initialValue() default "";

    boolean required() default false;

    boolean display() default true;

    /**
     * 决定配置的顺序， 默认-1 使用 class文件中field排序， 对于多继承结构可能不太满意
     * @return
     */
    int order() default -1;

    ConfigDataType configDataType() default ConfigDataType.TEXT;

    boolean isArray() default false;

    String group() default "";

    /**
     * 同组条件
     */
    String condition() default "";
}
