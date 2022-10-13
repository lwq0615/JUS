package com.jus.jdbc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此字段不参与数据库字段映射
 * 当某字段在数据库内没有对应字段时应该添加次注解，否则会抛出异常
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pass {
}
