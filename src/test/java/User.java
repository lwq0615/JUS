import lombok.Data;
import lwq.jdbc.annotation.Column;
import lwq.jdbc.annotation.Table;

@Table("users")
@Data
public class User {

    private Integer id;
    @Column("name")
    private String nam;

}
