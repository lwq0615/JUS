package lwq.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 用于配置实体类属性与数据库中表格字段的映射关系
 * 如果不配置，则默认使用属性名作为字段映射名称
 * @para：value 数据库对应表格字段名称
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String value();
}
