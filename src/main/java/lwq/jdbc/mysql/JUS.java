package lwq.jdbc.mysql;

import java.util.List;

public class JUS implements Execute {

    private static Execute jdbc;


    public JUS(String path) {
        if(jdbc == null){
            JDBCProxy jdbcProxy = new JDBCProxy(new JDBC(path));
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
    public <E> List<E> queryList(String sql, Class<E> rClass) {
        try {
            return jdbc.queryList(sql,rClass);
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
    public Integer insertReturnId(String sql) {
        try {
            return jdbc.insertReturnId(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <E extends Entity> Page<E> getPage(String sql, Class rClass, int current, int size) {
        try {
            return jdbc.getPage(sql, rClass, current, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询数据库中的一条记录4
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 返回查询结果，查询不到返回null
     */
    public <R extends Entity> R query(R obj){
        String sql = obj.selectSql();
        Class cls = obj.getClass();
        return (R)this.query(sql, cls);
    }

    /**
     * 查询数据库中的多条记录
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 返回查询结果，查询不到返回null
     */
    public <E extends Entity> List<E> queryList(E obj){
        String sql = obj.selectSql();
        Class cls = obj.getClass();
        return this.queryList(sql,cls);
    }

    /**
     * 分页查询
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @param current 当前页码
     * @param size 每页条数
     * @param <E> 返回数据类型
     * @return Page分页数据
     */
    public <E extends Entity> Page<E> getPage(E obj, int current, int size){
        String sql = obj.selectSql();
        return this.getPage(sql, obj.getClass(), current, size);
    }

    /**
     * 插入一条数据并返回自动递增的id
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为新增参数
     * @return 插入成功返回自动递增的id，否则返回null
     */
    public Integer insertReturnId(Entity obj){
        String sql = obj.insertSql();
        return this.insertReturnId(sql);
    }

    /**
     * 更新数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为更新参数，
     *            配置了@Id的属性将作为查询参数，不参与更新
     * @return 更新成功返回1，否则返回0
     */
    public int update(Entity obj){
        String sql = obj.updateSql();
        return this.execute(sql);
    }

    /**
     * 新增数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为新增参数
     * @return 新增成功返回1，否则返回0
     */
    public int insert(Entity obj){
        String sql = obj.insertSql();
        return this.execute(sql);
    }

    /**
     * 删除数据
     * @param obj 与数据库表格映射的实体类实例，实例的每个非null属性值将会作为查询参数
     * @return 删除成功返回1，否则返回0
     */
    public int delete(Entity obj){
        String sql = obj.deleteSql();
        return this.execute(sql);
    }
}
