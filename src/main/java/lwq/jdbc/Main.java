package lwq.jdbc;

import lwq.jdbc.mysql.JDBCUtil;
import lwq.jdbc.mysql.Page;

public class Main {
    public static void main(String[] args) {
        JDBCUtil jdbcUtil = new JDBCUtil("src/config.yml");
        User user = new User();
        jdbcUtil.setPage(2,3);
        Page page1 = jdbcUtil.getPage(user);
        jdbcUtil.setPage(1,3);
        Page page2 = jdbcUtil.getPage(user);
        System.out.println(page1);
        System.out.println(page2);
    }
}
