package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public Flux<News> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return newsService.findAll(PageWrapper.of(page, size));
    }

    @GetMapping("/news/user")
    public Flux<News> getAllNewsByUser(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return newsService.findAllByUser(userId, PageWrapper.of(page, size));
    }

    @PostMapping("/news/post")
    public Mono<ResponseEntity<News>> addNews(@RequestBody News news,@RequestBody String userId, @RequestBody List<String> paths) {
        if (news == null || news.getContent() == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        return newsService.saveNews(news, userId, paths)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().body(null));
    }
}
