package kz.marcy.endtermproject.Entity.Transient;

import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Users;
import lombok.Data;

import java.util.List;

@Data
public class ProfileDTO {
    private Users user; // Whose profile is this
    private String pathToAva; // Path to the avatar
    private List<News> news; // News that user has posted
    private boolean isFollowing; // Is the current user following this user
    private boolean isSelf; // Is the current user the same as the user in this profile
}
