package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Comments extends AbstractSuperClass {
    private String content; // Changeable -> user can edit the content
    private Users author; // Isn't changeable
    private News news; // Isn't changeable
}
