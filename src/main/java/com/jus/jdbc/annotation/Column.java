package com.jus.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于配置实体类属性与数据库中表格字段的映射关系
 * 如果不配置，则默认使用属性名作为字段映射名称
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * 数据库对应表格字段名称
     */
    String value();
}
