package kz.marcy.endtermproject.Service;

import jakarta.annotation.PostConstruct;
import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Repository.RolesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        if(areDefaultRolesCreated()){
            return;
        }
        log.info("Initing default roles");
        defaultRoles.forEach(role -> {
            if(!rolesRepo.existsByCode(role)){
                Roles newRole = new Roles();
                newRole.setName(role);
                newRole.setCode("ROLE_" + role);
                newRole.setCreatedAt(Instant.now());
                newRole.setUpdatedAt(null);
                newRole.setDeletedAt(null);
                rolesRepo.save(newRole);
            }
        });
    }

    public List<String> getDefaultRoleCodes() {
        return defaultRoles.stream()
                .map(role -> "ROLE_" + role)
                .collect(Collectors.toList());
    }

    public boolean areDefaultRolesCreated() {
        List<String> defaultRoleCodes = getDefaultRoleCodes();
        List<Roles> existingRoles = rolesRepo.findRolesByCodeIn(defaultRoleCodes);
        return existingRoles.size() == defaultRoleCodes.size();
    }

}
