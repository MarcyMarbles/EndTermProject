package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import kz.marcy.endtermproject.Entity.Transient.FIleDescriptorDTO;
import kz.marcy.endtermproject.Repository.FileDescriptorRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class FileDescriptorService extends AbstractSuperService<FileDescriptor> {
    private static final Logger log = LoggerFactory.getLogger(FileDescriptorService.class);
    @Autowired
    private FileDescriptorRepo fileDescriptorRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.default.upload-dir}")
    private String uploadDir;
    @Autowired
    private UserService userService;

    @Override
    public void saveEntity(FileDescriptor entity) {
        fileDescriptorRepo.save(entity).subscribe();
    }

    @Override
    public void softDelete(FileDescriptor entity) {
        super.softDelete(entity);
    }

    @Override
    public void softDelete(Iterable<FileDescriptor> entities) {
        super.softDelete(entities);
    }

    private String saveFileToDisk(byte[] file, String path) {
        try {
            if (Files.exists(Paths.get(path))) {
                if (Arrays.equals(Files.readAllBytes(Paths.get(path)), file)) {
                    log.info("File already exists: {}", path);
                    return path;
                }
                int i = 1;
                while (Files.exists(Paths.get(path + "_" + i))) {
                    i++;
                }
                path = path + "_" + i;
            }
            Files.write(Paths.get(path), file);
            return path;
        } catch (IOException e) {
            log.error("Error while saving file to disk", e);
            return null;
        }
    }


    public Mono<FileDescriptor> saveFile(FIleDescriptorDTO fIleDescriptorDTO) {
        if (fIleDescriptorDTO.getUserId() == null || fIleDescriptorDTO.getFile() == null) {
            return Mono.empty();
        }

        return userService.getUserById(fIleDescriptorDTO.getUserId())
                .flatMap(userId -> {
                    String path = uploadDir + "/" + userId.getId();
                    File directory = new File(path);

                    if (directory.mkdirs()) {
                        log.info("Directory created: {}", path);
                    }

                    File file = new File(directory, fIleDescriptorDTO.getName());
                    String newPath = saveFileToDisk(fIleDescriptorDTO.getFile(), file.getAbsolutePath());

                    if (newPath == null) {
                        return Mono.empty();
                    }

                    FileDescriptor fileDescriptor = new FileDescriptor();
                    fileDescriptor.setPath(newPath);
                    fileDescriptor.setUserId(userId.getId());
                    fileDescriptor.setName(fIleDescriptorDTO.getName());
                    fileDescriptor.setType(fIleDescriptorDTO.getType());
                    fileDescriptor.setSize(file.getTotalSpace());
                    fileDescriptor.setExtension(fIleDescriptorDTO.getExtension());


                    return fileDescriptorRepo.save(fileDescriptor);
                }).onErrorResume(e -> {
                    log.error("Error while saving file", e);
                    return Mono.empty();
                });
    }
}
