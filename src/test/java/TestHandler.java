import lwq.jdbc.mysql.AspectHandler;

import java.lang.reflect.InvocationTargetException;

public class TestHandler implements AspectHandler {
    @Override
    public void before(String sql) {
        System.out.println("开始查询");
    }

    @Override
    public void after(String sql) {
        System.out.println("查询结束");
    }

    @Override
    public void error(String sql, InvocationTargetException e) {
        System.out.println(e.getCause());
    }
}
