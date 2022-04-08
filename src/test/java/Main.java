import lwq.jdbc.mysql.*;
import lwq.jdbc.utils.ArrayUtils;

import javax.sql.rowset.JdbcRowSet;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        JDBCUtil jdbcUtil = new JDBCUtil("src/config.yml");
        System.out.println(jdbcUtil.getPage(new User(),1,1));
        System.out.println(jdbcUtil.queryList(new User()));

    }
}
