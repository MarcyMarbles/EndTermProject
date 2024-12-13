package kz.marcy.endtermproject.Entity;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@MappedSuperclass
public class AbstractSuperClass {
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
    private boolean isDeleted;
}
