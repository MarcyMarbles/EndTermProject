package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Comments;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CommentsRepo extends ReactiveMongoRepository<Comments, String> {
    @Override
    @Query("{ deletedAt :  null }")
    Flux<Comments> findAll();

    @Query("{ deletedAt : { $ne : null } }")
    Flux<Comments> findSoftDeleted();

    Flux<Comments> findByNewsIdAndDeletedAtIsNull(String newsId);

    Flux<Comments> findByAuthorIdAndDeletedAtIsNull(String authorId);

    Flux<Comments> findByAuthorIdAndNewsIdAndDeletedAtIsNull(String authorId, String newsId);

    Flux<Comments> findByAuthorIdAndNewsIdAndContentIsLikeIgnoreCaseAndDeletedAtIsNull(String authorId, String newsId, String content);

}
