import lombok.Data;
import lwq.jdbc.annotation.Column;
import lwq.jdbc.annotation.Table;
import lwq.jdbc.mysql.Entity;

@Table("users")
@Data
public class User extends Entity {

    private Integer id;
    @Column("name")
    private String nam;
}
