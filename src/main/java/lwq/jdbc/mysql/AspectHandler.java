package lwq.jdbc.mysql;


/**
 * 处理连接查询数据库时的各个切面
 */
public abstract class AspectHandler {

    /**
     * 查询前执行
     * @param sql 要执行的sql语句
     */
    public abstract void before(String sql);

    /**
     * 查询后执行
     * @param sql 执行的sql语句
     */
    public abstract void after(String sql);

}
