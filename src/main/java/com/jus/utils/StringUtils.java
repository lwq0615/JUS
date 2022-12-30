package com.jus.utils;

public class StringUtils {


    /**
     * 首字母大写
     */
    public static String firstUp(String str) {
        if(str == null){
            return null;
        }
        return str.substring(0, 1).toUpperCase()+str.substring(1);
    }


    /**
     * 首字母小写
     */
    public static String firstLow(String str) {
        if(str == null){
            return null;
        }
        return str.substring(0, 1).toLowerCase()+str.substring(1);
    }


}
