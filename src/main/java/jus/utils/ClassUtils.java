package jus.utils;

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


    /**
     * 获取当前类到其某个父类的所有属性
     * @param cls 类的class对象
     * @param stop 目标父类，该类及该类的父类属性不会被记入
     * @return Field数组
     */
    public static Field[] getFieldsToClass(Class cls, Class stop){
        if(cls == null || cls == stop){
            return null;
        }
        Field[] fields = cls.getDeclaredFields();
        return ArrayUtils.concat(fields,getFieldsToClass(cls.getSuperclass(),stop));
    }

}
