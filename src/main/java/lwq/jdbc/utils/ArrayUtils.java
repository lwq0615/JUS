package lwq.jdbc.utils;

import java.util.Arrays;
import java.util.List;

public class ArrayUtils {

    public static <T> T[] concat(T[] arr1, T[] arr2) {
        if(arr1 == null){
            return arr2;
        }
        if(arr2 == null){
            return arr1;
        }
        T[] res = Arrays.copyOf(arr1,arr1.length+arr2.length);
        System.arraycopy(arr2, 0, res, arr1.length, arr2.length);
        return res;
    }

    public static String join(List<String> arr, String c){
        String res = "";
        for (int i = 0; i < arr.size(); i++) {
            res += i==0 ? arr.get(i) : c+arr.get(i);
        }
        return res;
    }

}
