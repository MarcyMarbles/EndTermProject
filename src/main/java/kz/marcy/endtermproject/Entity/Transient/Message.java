package kz.marcy.endtermproject.Entity.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String type;
    private Object data; // When type is "BATCH" this will be a list of messages to be sent

    public static class Type {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String BATCH = "BATCH";
    }
}
