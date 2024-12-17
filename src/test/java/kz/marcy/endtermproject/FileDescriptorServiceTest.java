package kz.marcy.endtermproject;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import kz.marcy.endtermproject.Entity.Transient.FIleDescriptorDTO;
import kz.marcy.endtermproject.Repository.FileDescriptorRepo;
import kz.marcy.endtermproject.Service.FileDescriptorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileDescriptorServiceTest {

    private FileDescriptorService fileDescriptorService;
    private FileDescriptorRepo mockRepo;

    @BeforeEach
    void setUp() throws Exception {
        fileDescriptorService = new FileDescriptorService();

        mockRepo = mock(FileDescriptorRepo.class);

        Field repoField = FileDescriptorService.class.getDeclaredField("fileDescriptorRepo");
        repoField.setAccessible(true);
        repoField.set(fileDescriptorService, mockRepo);
    }

    @Test
    void testSaveFile() {
        FIleDescriptorDTO dto = new FIleDescriptorDTO();
        dto.setFile("file-content".getBytes());
        dto.setName("test.txt");

        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setName("test.txt");

        when(mockRepo.save(any(FileDescriptor.class))).thenReturn(Mono.just(fileDescriptor));

        StepVerifier.create(fileDescriptorService.saveFile(dto))
                .expectNextMatches(fd -> fd.getName().equals("test.txt"))
                .verifyComplete();

        verify(mockRepo, times(1)).save(any(FileDescriptor.class));
    }
}