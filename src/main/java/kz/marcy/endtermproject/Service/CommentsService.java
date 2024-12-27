package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Comments;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Repository.CommentsRepo;
import kz.marcy.endtermproject.WebSocketHandlers.CommentWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class CommentsService extends AbstractSuperService<Comments> {

    private final CommentsRepo commentsRepo;

    private final NewsService newsService;
    private final CommentWebSocketHandler commentWebSocketHandler;


    public CommentsService(CommentsRepo commentsRepo, NewsService newsService, CommentWebSocketHandler commentWebSocketHandler) {
        this.commentsRepo = commentsRepo;
        this.newsService = newsService;
        this.commentWebSocketHandler = commentWebSocketHandler;
    }

    public Mono<Comments> saveEntity(Comments entity) {
        return commentsRepo.save(entity);
    }

    public void softDelete(Comments entity) {
        super.softDelete(entity);
    }

    public void softDelete(Iterable<Comments> entities) {
        super.softDelete(entities);
    }

    public Flux<Comments> findAll(PageWrapper pageWrapper) {
        return commentsRepo.findAll()
                .skip((long) pageWrapper.getPage() * pageWrapper.getSize())
                .take(pageWrapper.getSize());
    }

    public Flux<Comments> findByNewsId(String newsId) {
        return commentsRepo.findByNewsIdAndDeletedAtIsNull(newsId); // all of the comments for a news
    }

    public Flux<Comments> findByAuthorId(String authorId) {
        return commentsRepo.findByAuthorIdAndDeletedAtIsNull(authorId); // all of the users comments
    }

    public Flux<Comments> findByAuthorIdAndNewsId(String authorId, String newsId) {
        return commentsRepo.findByAuthorIdAndNewsIdAndDeletedAtIsNull(authorId, newsId); // all of the users comments under a post
    }

    public Mono<Comments> addComment(Comments comment) {
        return newsService.findById(comment.getNewsId())
                .flatMap(news -> {
                    news.getComments().add(comment.getId());
                    return newsService.saveEntity(news).then(commentsRepo.save(comment));
                }).doOnSuccess(comments -> {
                    commentWebSocketHandler.publishCommentary(comments, Message.Type.ADD_COMMENT);
                });
    }

    public Mono<Comments> deleteComment(String commentId) {
        return commentsRepo.findById(commentId)
                .flatMap(comment -> {
                    comment.setDeletedAt(Instant.now());
                    return commentsRepo.save(comment);
                }).doOnSuccess(comments -> {
                    commentWebSocketHandler.publishCommentary(comments, Message.Type.DELETE_COMMENT);
                });
    }


}
