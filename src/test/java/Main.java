import lwq.jdbc.mysql.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        JUS jus = new JUS("src/config.yml");
        List<User> page = jus.queryList(new User());
        System.out.println(page);



    }
}
