import com.jus.jdbc.annotation.*;
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
