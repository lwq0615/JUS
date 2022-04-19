package lwq.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 标志一个属性为主键，在使用update(Entity)方法时，该属性会作为查询参数，而不会修改该字段的值
 * 同一个实体类可配置多个@Id
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
