import com.jus.jdbc.annotation.Column;
import com.jus.jdbc.annotation.Id;
import com.jus.jdbc.annotation.Pass;
import com.jus.jdbc.annotation.Table;
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
