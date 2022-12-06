package com.jus.jdbc.mysql;

import java.util.List;

public class JUS implements Execute {

    private static Execute jdbc;


    public JUS(String path) {
        if(jdbc == null){
            JDBCProxy jdbcProxy = new JDBCProxy(new ExecuteImpl(path));
            jdbc = jdbcProxy.getProxy();
        }
    }



    @Override
    public <R> R query(String sql, Class<R> rClass){
        try {
            return jdbc.query(sql,rClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <R> R query(String sql, List params, Class<R> rClass) {
        try {
            return jdbc.query(sql, params, rClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <E> List<E> queryList(String sql, Class<E> rClass) {
        try {
            return jdbc.queryList(sql,rClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <E> List<E> queryList(String sql, List params, Class<E> rClass) {
        try {
            return jdbc.queryList(sql, params, rClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int execute(String sql) {
        try {
            return jdbc.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int execute(String sql, List params) {
        try {
            return jdbc.execute(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Long insertReturnId(String sql) {
        try {
            return jdbc.insertReturnId(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long insertReturnId(String sql, List params) {
        try {
            return jdbc.insertReturnId(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询数据库中的一条记录
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 返回查询结果，查询不到返回null
     */
    public <R> R query(R obj){
        String sql = Entity.selectSql(obj);
        List params = Entity.getParams(obj, false);
        Class cls = obj.getClass();
        return (R)this.query(sql, params, cls);
    }

    /**
     * 查询数据库中的多条记录
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 返回查询结果，查询不到返回null
     */
    public <E> List<E> queryList(E obj){
        String sql = Entity.selectSql(obj);
        List params = Entity.getParams(obj, false);
        Class cls = obj.getClass();
        return this.queryList(sql, params, cls);
    }

    /**
     * 插入一条数据并返回自动递增的id
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为新增参数
     * @return 插入成功返回自动递增的id，否则返回null
     */
    public Long insertReturnId(Object obj){
        String sql = Entity.insertSql(obj);
        List params = Entity.getParams(obj, true);
        return this.insertReturnId(sql, params);
    }

    /**
     * 新增数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为新增参数
     * @return 新增成功返回1，否则返回0
     */
    public int insert(Object obj){
        String sql = Entity.insertSql(obj);
        List params = Entity.getParams(obj, true);
        return this.execute(sql, params);
    }

    /**
     * 更新数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为更新参数，
     *            配置了@Id的属性将作为查询参数，不参与更新
     * @return 更新成功返回1，否则返回0
     */
    public int update(Object obj){
        String sql = Entity.updateSql(obj);
        List params = Entity.getUpdateParams(obj);
        return this.execute(sql, params);
    }

    /**
     * 删除数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 删除成功返回1，否则返回0
     */
    public int delete(Object obj){
        String sql = Entity.deleteSql(obj);
        List params = Entity.getParams(obj, false);
        return this.execute(sql, params);
    }


    /**
     * 设置分页查询参数
     * @param current 当前页码
     * @param size 每页条数
     */
    public void setPage(int current, int size) {
        PageLimit.setLimit(current, size);
    }

}
