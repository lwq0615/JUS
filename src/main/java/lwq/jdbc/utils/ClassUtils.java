package lwq.jdbc.utils;

import java.lang.reflect.Field;

public class ClassUtils {


    /**
     * 获取某个类的所有属性（包括父类的属性）
     * @param cls 类的class对象
     * @return Field数组
     */
    public static Field[] getFields(Class cls){
        if(cls == null){
            return null;
        }
        Field[] fields = cls.getDeclaredFields();
        return ArrayUtils.concat(fields,getFields(cls.getSuperclass()));
    }

}
