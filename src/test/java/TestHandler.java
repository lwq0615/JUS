import jdbc.mysql.AspectHandler;

public class TestHandler implements AspectHandler {
    @Override
    public void before(Object[] args) {
        System.out.println("开始查询");
    }

    @Override
    public void after(Object[] args) {
        System.out.println("查询结束");
    }
}
