import lwq.jdbc.mysql.AspectHandler;

import java.lang.reflect.Method;

public class TestHandler extends AspectHandler {
    @Override
    public void before(String sql) {
        System.out.println("开始查询");
    }

    @Override
    public void after(String sql) {
        System.out.println("查询结束");
    }
}
