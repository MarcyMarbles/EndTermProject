package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Comments extends AbstractSuperClass {
    private String content; // Changeable -> user can edit the content
    private String authorId; // Isn't changeable
    private String newsId; // Isn't changeable
}
