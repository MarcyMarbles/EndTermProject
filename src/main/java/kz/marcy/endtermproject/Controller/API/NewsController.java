package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.NewsPostDTO;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Service.NewsService;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

    @PostMapping("/news/like/{id}")
    public Mono<ResponseEntity<News>> like(@RequestHeader("userId") String userId, @PathVariable String id){
        return null;
    }
}
