package kz.marcy.endtermproject.Entity.Transient;

import kz.marcy.endtermproject.Entity.Users;
import lombok.Data;

@Data
public class FriendsDTO {
    private Users user;
    private boolean isFriend;
}
