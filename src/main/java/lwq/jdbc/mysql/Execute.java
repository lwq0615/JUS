package lwq.jdbc.mysql;

import java.util.List;

public interface Execute {


    /**
     * 查询一条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    public <R> R query(String sql, Class<R> rClass);

    /**
     * 查询多条记录
     * @param sql 查询语句
     * @param rClass 映射类型
     * @return 查询结果
     */
    public <E> List<E> queryList(String sql, Class<E> rClass);

    /**
     * 增删改
     * @param sql sql语句
     * @return 操作成功的记录条数
     */
    public int execute(String sql);

    /**
     * 插入一条数据并返回自动递增的id
     * @param sql 插入语句
     * @return 插入成功返回自动递增的id，否则返回null
     */
    public Integer insertReturnId(String sql);

    /**
     * 查询total
     * @param sql 查询语句
     * @return total总条数
     */
    public int queryCount(String sql);

}
