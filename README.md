# JUS
 
### 如何使用

### 1.导入依赖jar包（mysql驱动包和JUS）

### 2.编写配置文件（yml格式）
```yml
jdbc:
  # 数据源
  datasource:
    driver: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jdbc?serverTimezone=GMT%2B8
    username: root
    password: 123456
  config:
    # 最大连接数
    maxCount: 100
    # 是否打印到控制台
    debug: true
    # 切面类
    aspectHandler: TestHandler
```

### 3.创建实体类
```java
import com.jus.jdbc.annotation.Column;
import com.jus.jdbc.annotation.Id;
import com.jus.jdbc.annotation.Pass;
import com.jus.jdbc.annotation.Table;
import lombok.Data;


// 数据库表名
@Table("users")
@Data
public class User {

    // 标记该字段为数据库主键
    @Id
    private Integer id;
    
    // 该字段与数据库表的字段名映射
    @Column("user_name")
    private String userName;
    
    // 该字段不作为增删改查的条件
    @Pass
    private String pass;
}
```

### 4.代码调用
```java
import com.jus.jdbc.mysql.JUS;
import com.jus.jdbc.mysql.Page;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JUS jus = new JUS("src/config.yml");
        User user = new User();
        jus.setPage(1, 10);
        List<User> users = jus.queryList(user);
        System.out.println(new Page<>(users));
    }
}

```

联系开发者：1072864729@qq.com
