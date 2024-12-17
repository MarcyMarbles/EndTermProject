package kz.marcy.endtermproject.ServicesTests;


import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Repository.NewsRepo;
import kz.marcy.endtermproject.Service.NewsService;
import kz.marcy.endtermproject.WebSocketHandlers.NewsWebSocketHandler;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.Mockito.*;

@SpringBootTest
public class NewsServiceTest {
    @Mock
    private NewsRepo newsRepo;

    @Mock
    private NewsWebSocketHandler newsWebSocketHandler;

    @InjectMocks
    private NewsService newsService;

    @Test
    public void testFindAll() {
        PageWrapper pageWrapper = PageWrapper.of(0, 2);
        News news1 = new News();
        News news2 = new News();

        when(newsRepo.findAll()).thenReturn(Flux.just(news1, news2));

        Flux<News> result = newsService.findAll(pageWrapper);

        StepVerifier.create(result)
                .expectNext(news1, news2)
                .verifyComplete();

        verify(newsRepo, times(1)).findAll();
    }

    @Test
    void testSoftDelete() {
        News news = new News();

        when(newsRepo.save(any(News.class))).thenReturn(Mono.just(news));

        newsService.softDelete(news);

        verify(newsWebSocketHandler, times(1))
                .publishNews(news, Message.Type.DELETE);

        verify(newsRepo, times(1)).save(news);
    }
}
