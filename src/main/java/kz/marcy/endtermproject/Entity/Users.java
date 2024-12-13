package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Users extends AbstractSuperClass {
    private String login;
    private String password;
    private String username; // User can change it anytime he wants
    private Roles roles;
}
