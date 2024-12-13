package kz.marcy.endtermproject.Controller;

import kz.marcy.endtermproject.Entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends MongoRepository<Users, UUID> {
    Page<Users> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Users> findByLogin(String login);
    
}
