package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Repository.FileDescriptorRepo;
import kz.marcy.endtermproject.Repository.NewsRepo;
import kz.marcy.endtermproject.Repository.UserRepo;
import kz.marcy.endtermproject.WebSocketHandlers.NewsWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService extends AbstractSuperService<News> {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);
    private final NewsRepo newsRepo;
    private final NewsWebSocketHandler newsWebSocketHandler;
    private final FileDescriptorRepo fileDescriptorRepo;
    private final UserRepo userRepo;
    private final UserService userService;

    public NewsService(NewsRepo newsRepo, NewsWebSocketHandler newsWebSocketHandler, FileDescriptorRepo fileDescriptorRepo, UserRepo userRepo, UserService userService) {
        this.newsRepo = newsRepo;
        this.newsWebSocketHandler = newsWebSocketHandler;
        this.fileDescriptorRepo = fileDescriptorRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @Override
    public void saveEntity(News entity) {
        newsRepo.save(entity).subscribe();
    }

    @Override
    public void softDelete(News entity) {
        super.softDelete(entity);
        newsWebSocketHandler.publishNews(entity, Message.Type.DELETE);
    }

    @Override
    public void softDelete(Iterable<News> entities) {
        super.softDelete(entities);
        entities.forEach(users -> newsWebSocketHandler.publishNews(users, Message.Type.DELETE));
    }

    public Flux<News> findAll(PageWrapper pageWrapper) {
        return newsRepo.findAll()
                .skip((long) pageWrapper.getPage() * pageWrapper.getSize())
                .take(pageWrapper.getSize());
    }

    public Flux<News> speciallyForYou(String userId, PageWrapper pageWrapper) {
        return userRepo.findById(userId)
                .flatMapMany(user -> newsRepo.findByAuthorIdIn(user.getFriends()))
                .skip((long) pageWrapper.getPage() * pageWrapper.getSize())
                .take(pageWrapper.getSize());
    }

    public Flux<News> findAllByUser(String userId, PageWrapper pageWrapper) {
        return newsRepo.findByAuthorIdAndDeletedAtIsNull(userId)
                .skip((long) pageWrapper.getPage() * pageWrapper.getSize())
                .take(pageWrapper.getSize())
                .doOnError(throwable -> log.error("Error while fetching news by user", throwable));
    }

    public Mono<News> saveNews(News news, String userId, List<String> paths) {
        return Flux.fromIterable(paths)
                .flatMap(fileDescriptorRepo::findAllByIdAndDeletedAtIsNull)
                .collectList()
                .flatMap(fileDescriptors -> {
                    if (news.getAttachments() == null) {
                        news.setAttachments(new ArrayList<>());
                    }
                    news.getAttachments().addAll(fileDescriptors);
                    return userService.getUserById(userId).flatMap(user -> {
                        news.setAuthor(user);
                        return newsRepo.save(news);
                    });
                }).flatMap(savedNews ->
                        userRepo.findById(userId).map(user -> {
                            savedNews.setAuthor(user);
                            return savedNews;
                        }))
                .doOnSuccess(savedNews -> newsWebSocketHandler.publishNews(savedNews, Message.Type.CREATE));
    }

    public Mono<News> like(String userId, String newsId) {
        return newsRepo.findByIdAndDeletedAtIsNull(newsId)
                .flatMap(news -> {
                    if (news.getLikes() == null) {
                        news.setLikes(new ArrayList<>());
                    }
                    if (news.getLikes().contains(userId)) {
                        news.getLikes().remove(userId);
                    } else {
                        news.getLikes().add(userId);
                    }
                    return newsRepo.save(news);
                }).doOnSuccess(news -> newsWebSocketHandler.publishNews(news, Message.Type.UPDATE));
    }

    public Flux<News> findByAuthorId(String authorId) {
        return newsRepo.findByAuthorIdAndDeletedAtIsNull(authorId);
    }

    public Mono<News> findById(String newsId){
        return newsRepo.findByIdAndDeletedAtIsNull(newsId);
    }
}
