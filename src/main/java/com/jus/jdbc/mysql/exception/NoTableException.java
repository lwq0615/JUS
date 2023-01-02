package com.jus.jdbc.mysql.exception;


/**
 * 实体类没有配置@Table注解时报错
 */
public class NoTableException extends Exception {

    private final static String NO_TABLE_MESSAGE = "Before using entity as parameters, " +
            "you need to configure @Table(tableName) annotation of the entity.";

    public NoTableException(){
        super(NO_TABLE_MESSAGE);
    }

}
