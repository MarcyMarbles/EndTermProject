package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.Transient.FriendsDTO;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Entity.Transient.ProfileDTO;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final RolesService rolesService;

    private final PendingService pendingService;
    private final NewsService newsService;

    public UserController(UserService userService, RolesService rolesService, PendingService pendingService, NewsService newsService) {
        this.userService = userService;
        this.rolesService = rolesService;
        this.pendingService = pendingService;
        this.newsService = newsService;
    }

    @GetMapping("/users")
    public Flux<Users> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return userService.findAll(PageWrapper.of(page, size));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> addUser(@RequestBody Users user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid user input"));
        }

        return rolesService.findByCode("ROLE_USER")
                .flatMap(role -> {
                    if (role == null) {
                        return Mono.just(ResponseEntity.badRequest().body("Role 'ROLE_USER' not found. Contact admin."));
                    }
                    user.setRoles(role);
                    user.setPending(true);
                    /*return pendingService.createPendingCode(user)
                            .flatMap(_ -> userService.saveUser(user)
                                    .then(Mono.just(ResponseEntity.ok("User registered successfully. Please confirm your email"))));*/
                    // Reverse since we need to save user first
                    return userService.saveUser(user)
                            .flatMap(savedUser -> pendingService.createPendingCode(savedUser)
                                    .then(Mono.just(ResponseEntity.ok("User registered successfully. Please confirm your email"))));

                })
                .defaultIfEmpty(ResponseEntity.badRequest().body("Role not found"));
    }

    @PostMapping("/updateUser")
    public Mono<ResponseEntity<String>> updateUser(@RequestBody Users user) {
        if (user == null || user.getId() == null) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid user input"));
        }

        return userService.updateUser(user)
                .then(Mono.just(ResponseEntity.ok("User updated successfully")));
    }

    @DeleteMapping("/deleteUser/{id}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id)
                .then(Mono.just(ResponseEntity.ok("User deleted successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error deleting user: " + e.getMessage())));
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private String trimToReact(String path) {
        int index = path.indexOf("assets");
        if (index == -1) {
            throw new IllegalArgumentException("The path does not contain 'assets': " + path);
        }
        return getRidOfBackslash(path.substring(index));
    }

    private String getRidOfBackslash(String path) {
        return path.replace("\\", "/");
    }


    @PostMapping("/user/profile/{username}") // User Accessing someone else profile
    public Mono<ResponseEntity<ProfileDTO>> getProfile(@PathVariable String username, @RequestHeader(value = "Authorization",
            required = false) String authorizationHeader) {
        if (username == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        String token = extractToken(authorizationHeader);
        return userService.findUserByUsername(username)
                .flatMap(user -> userService.findByToken(token)
                        .defaultIfEmpty(new Users())
                        .flatMap(currentUser -> newsService.findAllByUser(user.getId(), PageWrapper.of(0, 1000))
                                .collectList()
                                .flatMap(newsList -> {
                                    ProfileDTO profileDTO = new ProfileDTO();
                                    profileDTO.setUser(user);
                                    profileDTO.setNews(newsList);
                                    boolean isSelf = currentUser.getId() != null && currentUser.getId().equals(user.getId());
                                    profileDTO.setSelf(isSelf);
                                    if (user.getFriends() == null) {
                                        profileDTO.setFollowing(false);
                                    } else {
                                        profileDTO.setFollowing(user.getFriends().contains(currentUser.getId()));
                                    }
                                    return Mono.just(profileDTO);
                                })))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/friends/find")
    public Mono<ResponseEntity<Flux<FriendsDTO>>> findUser(@RequestParam String username, @RequestHeader("userId") String userId) {
        return userService.findUsersWithAlikeUsername(username)
                .filter(foundUser -> !foundUser.getId().equals(userId))
                .map(foundUser -> {
                    FriendsDTO friendsDTO = new FriendsDTO();
                    friendsDTO.setUser(foundUser);

                    if (foundUser.getFriends() == null) {
                        foundUser.setFriends(new ArrayList<>());
                    }

                    friendsDTO.setFriend(foundUser.getFriends().contains(userId));
                    return friendsDTO;
                })
                .collectList()
                .map(friendsList -> {
                    if (friendsList.isEmpty()) {
                        return ResponseEntity.notFound().build();
                    } else {
                        return ResponseEntity.ok(Flux.fromIterable(friendsList));
                    }
                });
    }


}
