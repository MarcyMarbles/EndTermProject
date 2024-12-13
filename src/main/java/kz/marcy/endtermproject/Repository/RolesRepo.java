package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Roles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolesRepo extends MongoRepository<Roles, String> {
    boolean existsByCode(String code);
    List<Roles> findRolesByCodeIn(List<String> codes);
}
