import lwq.jdbc.mysql.*;

public class Main {
    public static void main(String[] args) {

        JDBCUtil jdbcUtil = new JDBCUtil("src/config.yml");
        User page = jdbcUtil.query("select * from users",User.class);
        System.out.println(page);



    }
}
