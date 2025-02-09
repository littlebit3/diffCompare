package com.vt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于比较String类型的集合 比如 "1,2,3,4"，"张三,李四" ；separator配置分隔符
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CompareStringToCollection {

    String separator() default ",";
}
