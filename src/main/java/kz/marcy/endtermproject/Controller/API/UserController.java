package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Flux<Users> getAllUsers(){
        return userService.findAll();
    }
}
