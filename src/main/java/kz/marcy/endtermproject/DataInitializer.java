package kz.marcy.endtermproject;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.UserRepo;
import kz.marcy.endtermproject.Service.RolesService;
import kz.marcy.endtermproject.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolesService rolesService;
    private final UserRepo userRepo;
    private final UserService userService;

    public DataInitializer(RolesService rolesService, UserRepo userRepo, UserService userService) {
        this.rolesService = rolesService;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        Users user = new Users();
        user.setLogin("admin");
        user.setPassword("admin");
        user.setUsername("admin");
        rolesService.findByCode("ROLE_ADMIN")
                .flatMap(role -> {
                    user.setRoles(role);
                    return userRepo.findByLoginAndDeletedAtIsNull(user.getLogin())
                            .switchIfEmpty(userService.saveUser(user));
                }).subscribe();
    }
}

