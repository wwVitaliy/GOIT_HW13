import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Post {
    private int userId;
    private int id;
    private String title;
    private String body;
}

@Data
@AllArgsConstructor
class Comment {
    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;
}
