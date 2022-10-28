import com.jus.jdbc.mysql.JDBCProxy;
import com.jus.jdbc.mysql.JUS;
import com.jus.jdbc.mysql.Page;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JUS jus = new JUS("src/config.yml");
        User user = new User();
        user.setName("lwq");
        jus.setPage(1, 10);
        List<User> users = jus.queryList(user);
        System.out.println(users);
    }
}
