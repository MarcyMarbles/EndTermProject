package kz.marcy.endtermproject.EventListener;


import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Service.EmailService;
import kz.marcy.endtermproject.Service.PendingCodes;
import kz.marcy.endtermproject.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EntityEventListener implements ApplicationListener<MongoMappingEvent<?>> {


    private static final Logger log = LoggerFactory.getLogger(EntityEventListener.class);
    private final UserService userService;

    public EntityEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(MongoMappingEvent<?> event) {
        Object source = event.getSource();

        if (source instanceof AbstractSuperClass) {
            if (event instanceof BeforeConvertEvent) {
                handleBeforeConvert((AbstractSuperClass) source);
            } else if (event instanceof BeforeSaveEvent) {
                handleBeforeSave((AbstractSuperClass) source);
            } else if (event instanceof AfterSaveEvent) {
                handleAfterSave((AbstractSuperClass) source);
            } else if (event instanceof BeforeDeleteEvent) {
                handleBeforeDelete((AbstractSuperClass) source);
            } else if (event instanceof AfterDeleteEvent) {
                handleAfterDelete((AbstractSuperClass) source);
            }
        }
    }

    private void handleBeforeConvert(AbstractSuperClass entity) {
        log.info("Before converting document: {}", entity);
        if(entity.getCreatedAt() == null){
            entity.setCreatedAt(Instant.now());
        }else{
            entity.setUpdatedAt(Instant.now());
        }
    }

    private void handleBeforeSave(AbstractSuperClass entity) {
        log.info("Before saving document: {}", entity);
    }

    private void handleAfterSave(AbstractSuperClass entity) {
        log.info("After saving document: {}", entity);
        if(entity instanceof PendingCodes code){
            if(code.isUsed()){
                userService.confirmUser(code).subscribe();
            }
        }
    }

    private void handleBeforeDelete(AbstractSuperClass entity) {
        log.info("Before deleting document: {}", entity);
    }

    private void handleAfterDelete(AbstractSuperClass entity) {
        log.info("After deleting document: {}", entity);
    }
}
