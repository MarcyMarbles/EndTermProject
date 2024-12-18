package kz.marcy.endtermproject;

import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.UserRepo;
import kz.marcy.endtermproject.Service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Test
    void testFindByUsername() {
        UserRepo mockRepo = mock(UserRepo.class);
        CustomUserDetailsService service = new CustomUserDetailsService(mockRepo);

        Roles role = new Roles();
        role.setName("USER");

        // Создание пользователя
        Users user = new Users();
        user.setLogin("test");
        user.setPassword("password");
        user.setRoles(role);

        when(mockRepo.findByLoginAndDeletedAtIsNull("test"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(service.findByUsername("test"))
                .expectNextMatches(userDetails ->
                        userDetails.getUsername().equals("test") &&
                                userDetails.getPassword().equals("password"))
                .verifyComplete();

        verify(mockRepo).findByLoginAndDeletedAtIsNull("test");
    }
}