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
    private Object data;
    private Object receivers;

    public static class Type {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String BATCH = "BATCH"; // Ненужная
        public static final String ERROR = "ERROR";
        public static final String LIKE = "LIKE";
        public static final String ADD_COMMENT = "ADD_COMMENT";
        public static final String DELETE_COMMENT = "DELETE_COMMENT";
        public static final String FRIEND_REQUEST = "FRIEND_REQUEST";
        public static final String FRIEND_REQUEST_ACCEPTED = "FRIEND_REQUEST_ACCEPTED";
        public static final String FRIEND_REQUEST_REJECTED = "FRIEND_REQUEST_REJECTED";

    }
}
