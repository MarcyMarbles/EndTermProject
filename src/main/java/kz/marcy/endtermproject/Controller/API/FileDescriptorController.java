package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import kz.marcy.endtermproject.Entity.Transient.FIleDescriptorDTO;
import kz.marcy.endtermproject.Service.FileDescriptorService;
import kz.marcy.endtermproject.Service.UserService;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
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

    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<FileDescriptor>> uploadFile(
            @RequestPart("metadata") FIleDescriptorDTO fileDescriptorDTO,
            @RequestPart("file") FilePart file,
            @RequestParam Boolean isAvatar
    ) {
        return DataBufferUtils.join(file.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    fileDescriptorDTO.setFile(bytes); // set the file bytes into DTO
                    return fileDescriptorDTO;
                })
                .flatMap(descriptor -> {
                    if (isAvatar) {
                        return fileDescriptorService.saveFile(descriptor)
                                .flatMap(fd -> userService.setAvatar(descriptor.getUserId(), fd)
                                        .thenReturn(fd))
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.badRequest().build());
                    } else {
                        return fileDescriptorService.saveFile(descriptor)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.badRequest().build());
                    }
                })
                .onErrorResume(MultipartException.class, ex -> Mono.just(ResponseEntity.badRequest().build()));
    }


}
