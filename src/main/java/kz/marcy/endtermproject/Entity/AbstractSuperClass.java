package kz.marcy.endtermproject.Entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class AbstractSuperClass {
    private String id;
    private Instant createdAt;
    private Instant  updatedAt;
    private Instant  deletedAt;

    public boolean isDeleted(){
        return deletedAt != null;
    }
}
