package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class PendingCodes extends AbstractSuperClass {
    private String code;
    private String userId;
    private String email;
    private boolean used;
    private Instant dueDate;
}
