package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class News extends AbstractSuperClass {
    private String content; // Changeable -> user can edit the content
    private List<FileDescriptor> attachments; // null // Changeable -> user can delete or add new attachments
    private Users author; // Null Isn't changeable
    private List<String> likes; // Null Changeable -> user can like or dislike the news // id of the user
    private List<String> dislikes; // Null Changeable -> user can like or dislike the news // id of the user
    private List<String> comments; // Null Changeable -> user can add or delete comments // id of the comment
    @Transient
    private int likesCount;
    @Transient
    private int dislikesCount;
    @Transient
    private int commentsCount;

    @Transient
    public int getLikesCount() {
        if(likes == null) return 0;
        return likes.size();
    }

    @Transient
    public int getDislikesCount() {
        if(dislikes == null) return 0;
        return dislikes.size();
    }

    @Transient
    public int getCommentsCount() {
        if(comments == null) return 0;
        return comments.size();
    }
}
