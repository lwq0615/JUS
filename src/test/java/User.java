import jdbc.annotation.Column;
import jdbc.annotation.Id;
import jdbc.annotation.Pass;
import jdbc.annotation.Table;
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
