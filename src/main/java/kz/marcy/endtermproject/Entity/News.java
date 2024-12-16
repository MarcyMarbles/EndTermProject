package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class News extends AbstractSuperClass{
    private String content; // Changeable -> user can edit the content
    private List<FileDescriptor> attachments; // null // Changeable -> user can delete or add new attachments
    private Users author; // Null Isn't changeable
    private List<Users> likes; // Null Changeable -> user can like or dislike the news
    private List<Users> dislikes; // Null Changeable -> user can like or dislike the news
    private List<Comments> comments; // Null Changeable -> user can add or delete comments
}
