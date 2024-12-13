package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.UserRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo usersRepository;

    public CustomUserDetailsService(UserRepo usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Users user = usersRepository.findByLogin(login)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));

        return User.builder()
            .username(user.getLogin())
            .password(user.getPassword())
            .roles(user.getRoles().getName())
            .build();
    }
}
