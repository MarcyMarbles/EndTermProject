package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Users extends AbstractSuperClass {
    private String login; // Non changing field
    private String password; // Changeable field
    private String username; //  Changeable field
    private String email; // Changeable field
    private Roles roles; // Changeable -> but only by user with role ADMIN
    private FileDescriptor avatar; // Changeable -> storing the story in the FS and the path in the DB
    private List<String> friends; // Changeable -> user can add or delete friends // Friends are stored as a list of user ids
    // If user is a group, then friends are stored as a list of group members
    private boolean isGroup; // null Non changing field -> If user created a group, then it is a group
    private boolean isPending; // Changeable -> If user is pending, then he can't login, he should confirm his email
}
