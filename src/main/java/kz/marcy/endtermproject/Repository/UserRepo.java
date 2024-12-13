package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends MongoRepository<Users, String> {
    Page<Users> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Users> findByLogin(String login);

}
