package kz.marcy.endtermproject.EventListener;


import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EntityEventListener implements ApplicationListener<MongoMappingEvent<?>> {

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
        System.out.println("Before converting document: " + entity);
        if(entity.getCreatedAt() == null){
            entity.setCreatedAt(Instant.now());
        }else{
            entity.setUpdatedAt(Instant.now());
        }
    }

    private void handleBeforeSave(AbstractSuperClass entity) {
        System.out.println("Before saving document: " + entity);
    }

    private void handleAfterSave(AbstractSuperClass entity) {
        System.out.println("After saving document: " + entity);
    }

    private void handleBeforeDelete(AbstractSuperClass entity) {
        System.out.println("Before deleting document: " + entity);
    }

    private void handleAfterDelete(AbstractSuperClass entity) {
        System.out.println("After deleting document: " + entity);
    }
}
