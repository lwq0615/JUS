package jdbc.annotation;

import java.lang.annotation.*;


/**
 * 用于配置实体类与数据库表格映射关系
 * 建议配置，不配置将无法使用该类示例作为参数进行增删改查
 * @param：value 数据库对应表格名称
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value();
}
