import jus.jdbc.annotation.Column;
import jus.jdbc.annotation.Id;
import jus.jdbc.annotation.Pass;
import jus.jdbc.annotation.Table;
import lombok.Data;

@Table("users")
@Data
public class User {

    @Id
    private Integer id;
    @Column("name")
    private String name;
    @Pass
    private String pass;
}
