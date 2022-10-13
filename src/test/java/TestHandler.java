import jdbc.mysql.AspectHandler;

public class TestHandler implements AspectHandler {
    @Override
    public void before(String sql) {
        System.out.println("开始查询");
    }

    @Override
    public void after(String sql) {
        System.out.println("查询结束");
    }
}
