package kz.marcy.endtermproject.Service;

import jakarta.annotation.PostConstruct;
import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Repository.RolesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class RolesService extends AbstractSuperService<Roles> {
    @Value("${app.default.roles}")
    private List<String> defaultRoles;

    private final RolesRepo rolesRepo;

    public RolesService(RolesRepo rolesRepo) {
        this.rolesRepo = rolesRepo;
    }

    @PostConstruct
    public void initRoles() {
        areDefaultRolesCreated()
                .filter(created -> !created)
                .flatMapMany(ignored -> Flux.fromIterable(defaultRoles))
                .flatMap(role -> rolesRepo.existsByCodeAndDeletedAtIsNull(role)
                        .filter(exists -> !exists)
                        .map(exists -> {
                            Roles newRole = new Roles();
                            newRole.setName(role);
                            newRole.setCode("ROLE_" + role);
                            newRole.setCreatedAt(Instant.now());
                            return newRole;
                        })
                        .flatMap(rolesRepo::save))
                .subscribe();
    }

    public Flux<String> getDefaultRoleCodes() {
        return Flux.fromIterable(defaultRoles)
                .map(role -> "ROLE_" + role);
    }

    public Mono<Boolean> areDefaultRolesCreated() {
        return getDefaultRoleCodes()
                .collectList()
                .flatMap(codes -> rolesRepo.findRolesByCodeInAndDeletedAtIsNull(codes)
                        .collectList()
                        .map(existingRoles -> existingRoles.size() == codes.size()));
    }

    public Mono<Roles> findByCode(String code) {
        return rolesRepo.findByCodeAndDeletedAtIsNull(code);
    }

    @Override
    public Mono<Roles> saveEntity(Roles entity) {
        return rolesRepo.save(entity);// Save the entity reactively
    }


}
