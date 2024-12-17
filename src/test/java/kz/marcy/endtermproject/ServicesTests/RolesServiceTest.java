package kz.marcy.endtermproject.ServicesTests;

import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Repository.RolesRepo;
import kz.marcy.endtermproject.Service.RolesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class RolesServiceTest {

    @Mock
    private RolesRepo rolesRepo;

    @InjectMocks
    private RolesService rolesService;

    private List<String> defaultRoles = Arrays.asList("ADMIN", "USER", "MODERATOR");

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Use reflection to set the private defaultRoles field in RolesService
        Field defaultRolesField = RolesService.class.getDeclaredField("defaultRoles");
        defaultRolesField.setAccessible(true);  // Make it accessible
        defaultRolesField.set(rolesService, defaultRoles);  // Set the field value
    }


    @Test
    void testGetDefaultRoleCodes() {
        // Act
        Flux<String> roleCodesFlux = rolesService.getDefaultRoleCodes();

        // Assert
        StepVerifier.create(roleCodesFlux)
                .expectNext("ROLE_ADMIN")
                .expectNext("ROLE_USER")
                .expectNext("ROLE_MODERATOR")
                .verifyComplete();
    }

    @Test
    void testAreDefaultRolesCreated_whenRolesExist() {
        // Arrange: Mock behavior for checking if roles exist
        when(rolesRepo.findRolesByCodeInAndDeletedAtIsNull(anyList())).thenReturn(Flux.just(new Roles(), new Roles(), new Roles()));

        // Act: Call the method to check if the default roles are created
        Mono<Boolean> result = rolesService.areDefaultRolesCreated();

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testFindByCode_whenRoleExists() {
        // Arrange
        Roles role = new Roles();
        role.setCode("ROLE_ADMIN");
        when(rolesRepo.findByCodeAndDeletedAtIsNull("ROLE_ADMIN")).thenReturn(Mono.just(role));

        // Act
        Mono<Roles> result = rolesService.findByCode("ROLE_ADMIN");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(r -> r.getCode().equals("ROLE_ADMIN"))
                .verifyComplete();
    }

    @Test
    void testSaveEntity_shouldSaveRole() {
        // Arrange
        Roles role = new Roles();
        role.setCode("ROLE_NEW");
        role.setCreatedAt(Instant.now());
        when(rolesRepo.save(any(Roles.class))).thenReturn(Mono.just(role));

        // Act
        rolesService.saveEntity(role);

        // Assert
        verify(rolesRepo, times(1)).save(role);
    }
}

