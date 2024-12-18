package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import kz.marcy.endtermproject.Entity.Transient.FIleDescriptorDTO;
import kz.marcy.endtermproject.Service.FileDescriptorService;
import kz.marcy.endtermproject.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class FileDescriptorController {

    private final FileDescriptorService fileDescriptorService;
    private final UserService userService;

    public FileDescriptorController(FileDescriptorService fileDescriptorService, UserService userService) {
        this.fileDescriptorService = fileDescriptorService;
        this.userService = userService;
    }

    @PostMapping("/files/upload")
    public Mono<ResponseEntity<FileDescriptor>> uploadFile(FIleDescriptorDTO fileDescriptorDTO, @RequestParam Boolean isAvatar) {
        if (isAvatar) {
            return fileDescriptorService.saveFile(fileDescriptorDTO)
                    .flatMap(fileDescriptor -> userService.setAvatar(fileDescriptorDTO.getUserId(), fileDescriptor)
                            .flatMap(_ -> Mono.just(fileDescriptor)))
                    .flatMap(fileDescriptor -> Mono.just(ResponseEntity.ok(fileDescriptor)))
                    .defaultIfEmpty(ResponseEntity.badRequest().build());
        }
        return fileDescriptorService.saveFile(fileDescriptorDTO)
                .flatMap(fileDescriptor -> Mono.just(ResponseEntity.ok(fileDescriptor)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


}
