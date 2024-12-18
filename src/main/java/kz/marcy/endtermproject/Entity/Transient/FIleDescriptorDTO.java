package kz.marcy.endtermproject.Entity.Transient;

import lombok.Data;

@Data
public class FIleDescriptorDTO {
    private String name; // Name of the file
    private String type; // Type of the file -> text, image, video, audio, etc.
    private String extension; // Extension of the file -> txt, jpg, mp4, mp3, etc.
    private String userId; // User Id -> who uploaded the file
    private byte[] file; // File itself
}

