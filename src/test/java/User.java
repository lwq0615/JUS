import lombok.Data;
import lwq.jdbc.annotation.Column;
import lwq.jdbc.annotation.Pass;
import lwq.jdbc.annotation.Table;
import lwq.jdbc.mysql.Entity;
import lwq.jdbc.mysql.JDBCProxy;

import java.util.Date;

@Table("users")
@Data
public class User extends Entity {

    private Integer id;
    @Column("name")
    private String nam;
    private Date time;
    @Pass
    private String a;
}
