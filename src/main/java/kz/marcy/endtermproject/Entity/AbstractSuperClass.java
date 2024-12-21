package kz.marcy.endtermproject.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public abstract class AbstractSuperClass {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
