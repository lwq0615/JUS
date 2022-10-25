package com.jus.jdbc.mysql;

import java.util.List;

public interface Execute {


    /**
     * 查询一条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    <R> R query(String sql, Class<R> rClass) throws Exception;


    /**
     * 预编译查询
     */
    <R> R query(String sql, List params, Class<R> rClass) throws Exception;

    /**
     * 查询多条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    <E> List<E> queryList(String sql, Class<E> rClass) throws Exception;

    /**
     * 预编译查询
     */
    <E> List<E> queryList(String sql, List params, Class<E> rClass) throws Exception;

    /**
     * 增删改
     * @param sql sql语句
     * @return 操作成功的记录条数
     */
    int execute(String sql) throws Exception;

    /**
     * 预编译增删改
     */
    int execute(String sql, List params) throws Exception;

    /**
     * 插入一条数据并返回自动递增的id
     * @param sql 插入语句
     * @return 插入成功返回自动递增的id，否则返回null
     */
    Long insertReturnId(String sql) throws Exception;

    /**
     * 预编译版本返回id
     */
    Long insertReturnId(String sql, List params) throws Exception;

}
