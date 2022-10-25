import com.jus.jdbc.mysql.JUS;
import com.jus.jdbc.mysql.Page;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JUS jus = new JUS("src/config.yml");
        User user = new User();
        user.setName("123");
        jus.setPage(1, 2);
        System.out.println(jus.insertReturnId(user));
    }
}
