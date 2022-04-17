import lwq.jdbc.mysql.*;

public class Main {
    public static void main(String[] args) {

        JDBCUtil jdbcUtil = new JDBCUtil("src/config.yml");
        Page<User> page = jdbcUtil.getPage(new User(), 1, 3);

    }
}
