package jdbc.mysql;


import java.lang.reflect.InvocationTargetException;

/**
 * 处理连接查询数据库时的各个切面
 */
public interface AspectHandler {

    /**
     * 查询前执行
     * @param sql 要执行的sql语句
     */
    default void before(String sql){}

    /**
     * 查询后执行
     * @param sql 执行的sql语句
     */
    default void after(String sql){}

    /**
     * 执行sql语句报错时执行
     * @param sql 执行的sql语句
     * @param e 错误信息
     * @return return为false时不打印错误信息
     */
    default boolean error(String sql, InvocationTargetException e){
        return true;
    }

}
