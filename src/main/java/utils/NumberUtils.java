package utils;

import java.util.Arrays;
import java.util.List;

public class NumberUtils {

    private static List<Class> NUMBER_CLASS = Arrays.asList(byte.class,
            short.class, int.class, long.class, float.class, double.class);


    public static boolean isNumber(Object obj){
        if(obj == null){
            return false;
        }
        return Number.class.isAssignableFrom(obj.getClass()) || NUMBER_CLASS.contains(obj.getClass());
    }

    public static boolean isFloat(Object obj){
        if(obj == null){
            return false;
        }
        return obj.getClass() == float.class || obj.getClass() == Float.class;
    }

    public static boolean isDouble(Object obj){
        if(obj == null){
            return false;
        }
        return obj.getClass() == double.class || obj.getClass() == Double.class;
    }

}
