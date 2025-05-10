package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Balance;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceRepo extends ReactiveMongoRepository<Balance, String> {
    Flux<Balance> findByOwnerId(String ownerId); // Метод для поиска баланса по ID владельца
    
    Flux<Balance> findByOwnerIdIn(List<String> ownerIds); // Метод для поиска баланса по списку ID владельцев
    
    Flux<Balance> findByCurrency(String currency); // Метод для поиска баланса по валюте
    
    Flux<Balance> findByOwnerIdAndCurrency(String ownerId, String currency); // Метод для поиска баланса по ID владельца и валюте
    
    Flux<Balance> findByOwnerIdInAndCurrency(List<String> ownerIds, String currency); // Метод для поиска баланса по списку ID владельцев и валюте


}
