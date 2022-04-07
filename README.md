# JDBCUtils
 
### 如何使用

### 1.导入依赖jar包（mysql驱动包和JDBCUtils）

### 2.编写配置文件（yml格式）
```yml
#数据库配置
datasource:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost:3306/jdbc?serverTimezone=GMT%2B8
  username: root
  password: 123456
  #最大连接数
  maxCount: 100
```

### 3.创建实体类
```java
import lwq.jdbc.annotation.Id;
import lwq.jdbc.annotation.Table;

@Data
//与该实体映射的表格名称
@Table("users")
public class User {
    //在使用update方法更新数据时，添加了@Id的属性将被作为查询参数
    @Id
    private Integer id;
    //可通过@Column注解配置与数据库映射的字段，没有配置则默认使用属性名
    @Column("user_name")
    private String userName;
    private int age;  
}
```

### 4.代码调用
```java
import lwq.jdbc.mysql.JDBCUtil;

public class Main {
    public static void main(String[] args) {
        //以配置文件的路径为构造参数
        JDBCUtil jdbcUtil = new JDBCUtil("src/config.yml");
        
        //通过配置的实体类查询
        User user = new User();
        user.setId(1);
        user = jdbcUtil.query(user);
        System.out.println(user);

        //分页查询
        jdbcUtil.setPage(2,10);
        Page page = jdbcUtil.getPage(user);
        System.out.println(page);

    }
}
```

联系开发者：1072864729@qq.com
