package lwq.utils;

import java.util.ArrayList;
import java.util.List;

public class NumberUtils {

    private static List<Class> NUMBER_CLASS;

    static {
        NUMBER_CLASS = new ArrayList<Class>();
        NUMBER_CLASS.add(byte.class);
        NUMBER_CLASS.add(short.class);
        NUMBER_CLASS.add(int.class);
        NUMBER_CLASS.add(long.class);
        NUMBER_CLASS.add(float.class);
        NUMBER_CLASS.add(double.class);
    }

    public static boolean isNumber(Object obj){
        if(obj == null){
            return false;
        }
        return Number.class.isAssignableFrom(obj.getClass()) || NUMBER_CLASS.contains(obj.getClass());
    }

}
