package com.jus.jdbc.mysql.exception;


/**
 * 添加@Required注解的属性，在新增和编辑时，如果该属性值为空，则会抛出该异常
 */
public class NoValueException extends RuntimeException {

    public NoValueException(String className, String fieldName){
        super("The field '"+fieldName+"' of Class '"+className+"' cannot be null when insert or update");
    }

}
