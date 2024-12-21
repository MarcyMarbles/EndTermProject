package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.Comments;
import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.NewsPostDTO;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public Mono<ResponseEntity<List<News>>> getAllNews(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return newsService.findAll(PageWrapper.of(page, size))
                .collectList()
                .map(newsList -> newsList.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(newsList));
    }

    @GetMapping("/news/speciallyForYou")
    public Mono<ResponseEntity<List<News>>> getAllNewsByUser(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return newsService.speciallyForYou(userId, PageWrapper.of(page, size))
                .collectList()
                .map(newsList -> newsList.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(newsList));
    }

    @PostMapping("/news/post")
    public Mono<ResponseEntity<News>> addNews(@RequestBody NewsPostDTO news,
                                              @RequestHeader("userId") String userId) {
        if (news == null || news.getNewsDTO() == null || news.getNewsDTO().getContent() == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        return newsService.saveNews(news.getNewsDTO(), userId, List.of(news.getIds()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().body(null));
    }

    @GetMapping("/news/{newsId}")
    public Mono<ResponseEntity<News>> getNewsById(@PathVariable String newsId) {
        if (newsId == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return newsService.findById(newsId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().body(null));
    }

/*
    @GetMapping("/news/{newsId}/comments")
    public Mono<ResponseEntity<List<Comments>>> getComments(@PathVariable String newsId) {
        if (newsId == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return newsService.getComments(newsId)
                .collectList()
                .map(comments -> comments.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(comments));
    }

    @PostMapping("/news/{newsId}/comment")
    public Mono<ResponseEntity<Comments>> addComment(@RequestHeader("userId") String userId,
                                                     @PathVariable String newsId,
                                                     @RequestBody Comments comment) {
        if (newsId == null || userId == null || comment == null || comment.getContent() == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return newsService.addComment(newsId, userId, comment)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().body(null));
    }

    @DeleteMapping("/news/{newsId}/comment/{commentId}")
    public Mono<ResponseEntity<Comments>> deleteComment(@RequestHeader("userId") String userId,
                                                        @PathVariable String newsId,
                                                        @PathVariable String commentId) {
        if (newsId == null || userId == null || commentId == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return newsService.deleteComment(newsId, userId, commentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().body(null));
    }
*/

    @GetMapping("/news/{authorId}")
    public Mono<ResponseEntity<List<News>>> getNewsByAuthor(@PathVariable String authorId) {
        if (authorId == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return newsService.findByAuthorId(authorId)
                .collectList()
                .map(newsList -> newsList.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok((newsList)));
    }


    @PostMapping("/news/{newsId}/like")
    public Mono<ResponseEntity<News>> like(@RequestHeader("userId") String userId,
                                           @PathVariable String newsId) {
        if (newsId == null || userId == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        return newsService.like(newsId, userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().body(null));
    }
}
