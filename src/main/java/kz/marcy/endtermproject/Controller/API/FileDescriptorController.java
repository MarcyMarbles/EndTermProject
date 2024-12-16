package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import kz.marcy.endtermproject.Entity.Transient.FIleDescriptorDTO;
import kz.marcy.endtermproject.Service.FileDescriptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FileDescriptorController {

    @Autowired
    private FileDescriptorService fileDescriptorService;

    @PostMapping("/files/upload")
    public Mono<ResponseEntity<FileDescriptor>> uploadFile(FIleDescriptorDTO fileDescriptorDTO) {
        return fileDescriptorService.saveFile(fileDescriptorDTO)
                .flatMap(fileDescriptor -> Mono.just(ResponseEntity.ok(fileDescriptor)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

}
