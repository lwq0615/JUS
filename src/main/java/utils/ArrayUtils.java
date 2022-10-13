package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {

    public static <T extends Object> T[] concat(T[] ...arrs) {
        T[] res = null;
        for (int i = 0; i < arrs.length; i++) {
            if(arrs[i] == null){
                continue;
            }
            if(res == null){
                res = arrs[i];
                continue;
            }
            res = Arrays.copyOf(res,res.length+arrs[i].length);
            System.arraycopy(arrs[i], 0, res, res.length-arrs[i].length, arrs[i].length);
        }
        return res;
    }

    public static String join(List<String> arr, String c){
        String res = "";
        res += arr.get(0);
        for (int i = 1; i < arr.size(); i++) {
            res += c+arr.get(i);
        }
        return res;
    }

    public static <T> List<T> asArray(T ...arr){
        List<T> list = new ArrayList<>();
        for (T t : arr) {
            list.add(t);
        }
        return list;
    }

}
