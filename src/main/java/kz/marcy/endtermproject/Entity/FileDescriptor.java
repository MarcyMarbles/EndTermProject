package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class FileDescriptor extends AbstractSuperClass {
    private String name; // Name of the file
    private String type; // Type of the file -> text, image, video, audio, etc.
    private String path; // Path to the file in the FS
    private long size; // Size of the file
    private String extension; // Extension of the file -> txt, jpg, mp4, mp3, etc.
    private String userId; // User Id -> who uploaded the file
}
