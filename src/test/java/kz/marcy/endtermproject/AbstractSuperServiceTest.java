package kz.marcy.endtermproject;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Service.AbstractSuperService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractSuperServiceTest {

    static class TestEntity extends AbstractSuperClass {}

    static class TestService extends AbstractSuperService<TestEntity> {
        @Override
        public void saveEntity(TestEntity entity) {
        }
    }

    @Test
    void testSoftDelete() {
        TestService service = spy(new TestService());
        TestEntity entity = new TestEntity();

        service.softDelete(entity);
        assertNotNull(entity.getDeletedAt());
        verify(service, times(1)).saveEntity(entity);
    }

    @Test
    void testSoftDeleteWithInvalidEntity() {
        AbstractSuperService<Object> service = new AbstractSuperService<>() {
            @Override
            public void saveEntity(Object entity) {
            }
        };

        Object invalidEntity = new Object();
        assertThrows(IllegalArgumentException.class, () -> service.softDelete(invalidEntity));
    }
}