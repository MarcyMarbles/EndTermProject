package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Users;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NewsRepo extends ReactiveMongoRepository<News, String> {
    @Query("{deletedAt :  null}")
    Flux<News> findAll();

    @Query("{ deletedAt : { $ne : null } }")
    Flux<News> findSoftDeleted();

    Flux<News> findByAuthorIdAndDeletedAtIsNull(String authorId);

}
