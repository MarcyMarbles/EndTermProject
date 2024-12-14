package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.UserRepo;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo usersRepository) {
        this.userRepo = usersRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByLogin(username)
            .map(user -> User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles().getName())
                .build()
            );
    }
}
