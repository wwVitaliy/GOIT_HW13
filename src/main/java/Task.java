import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private int userId;
    private int id;
    private String title;
    private boolean completed;
}
