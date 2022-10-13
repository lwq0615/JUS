import jus.jdbc.mysql.JUS;
import jus.jdbc.mysql.Page;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JUS jus = new JUS("src/config.yml");
        User user = new User();
        user.setA("123");
        jus.setPage(1, 2);
        List<User> users = jus.queryList(user);
        System.out.println(new Page<>(users));
    }
}
