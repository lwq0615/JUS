package lwq.jdbc;


import lombok.Data;
import lwq.jdbc.annotation.Table;

@Table("users")
@Data
public class User {

    private Integer id;
    private String name;

}
