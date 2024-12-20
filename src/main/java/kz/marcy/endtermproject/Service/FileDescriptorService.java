package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import kz.marcy.endtermproject.Entity.Transient.FIleDescriptorDTO;
import kz.marcy.endtermproject.Repository.FileDescriptorRepo;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class FileDescriptorService {

    private static final Logger log = LoggerFactory.getLogger(FileDescriptorService.class);
    private final Path uploadDir;
    private final FileDescriptorRepo fileDescriptorRepo;
    private final UserService userService;

    public FileDescriptorService(FileDescriptorRepo fileDescriptorRepo,
                                 UserService userService,
                                 @Value("${app.default.upload-dir}") String uploadDir) {
        this.fileDescriptorRepo = fileDescriptorRepo;
        this.userService = userService;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
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

    private byte[] resizeImage(byte[] originalImage, int width, int height) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(originalImage);
        BufferedImage originalBufferedImage = ImageIO.read(inputStream);
        BufferedImage resizedBufferedImage = new BufferedImage(width, height, originalBufferedImage.getType());
        Graphics2D g = resizedBufferedImage.createGraphics();
        g.drawImage(originalBufferedImage, 0, 0, width, height, null);
        g.dispose();

        Path tempFile = Files.createTempFile("resized", ".tmp");
        ImageIO.write(resizedBufferedImage, "jpg", tempFile.toFile());

        byte[] resizedBytes = Files.readAllBytes(tempFile);
        Files.delete(tempFile);
        return resizedBytes;
    }

    public Mono<FileDescriptor> saveFile(FIleDescriptorDTO fileDescriptorDTO) {
    if (fileDescriptorDTO.getUserId() == null || fileDescriptorDTO.getFile() == null) {
        return Mono.empty();
    }

    return userService.getUserById(fileDescriptorDTO.getUserId())
            .flatMap(userId -> {
                String userDirectory = uploadDir + "/" + userId.getId();
                Path userDirPath = Paths.get(userDirectory);

                return Mono.fromCallable(() -> {
                    if (!Files.exists(userDirPath)) {
                        Files.createDirectories(userDirPath);
                        log.info("Directory created: {}", userDirectory);
                    }
                    return userDirPath;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(_ -> {
                    String originalFilePath = userDirectory + "/" + fileDescriptorDTO.getName();
                    String newPath = saveFileToDisk(fileDescriptorDTO.getFile(), originalFilePath);

                    if (newPath == null) {
                        return Mono.empty();
                    }

                    List<ImageSize> sizes = List.of(
                            ImageSize.of(100, 100),
                            ImageSize.of(200, 200),
                            ImageSize.of(400, 400)
                    );

                    for (ImageSize size : sizes) {
                        try {
                            byte[] resizedImage = resizeImage(fileDescriptorDTO.getFile(), size.getWidth(), size.getHeight());
                            String ext = "." + fileDescriptorDTO.getExtension();
                            String resizedFilePath = originalFilePath.replace(ext, "_" + size.getWidth() + "x" + size.getHeight() + ext);
                            saveFileToDisk(resizedImage, resizedFilePath);
                        } catch (IOException e) {
                            log.error("Error resizing image to {}x{}", size.getWidth(), size.getHeight(), e);
                        }
                    }

                    FileDescriptor fileDescriptor = new FileDescriptor();
                    fileDescriptor.setPath(newPath);
                    fileDescriptor.setUserId(userId.getId());
                    fileDescriptor.setName(fileDescriptorDTO.getName());
                    fileDescriptor.setType(fileDescriptorDTO.getType());
                    fileDescriptor.setSize(new File(newPath).length());
                    fileDescriptor.setExtension(fileDescriptorDTO.getExtension());

                    return fileDescriptorRepo.save(fileDescriptor);
                });
            }).onErrorResume(e -> {
                log.error("Error while saving file", e);
                return Mono.empty();
            });
}

    public Resource getFile(String userId, String filename) {
        try {
            Path filePath = this.uploadDir.resolve(userId).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found for user " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve file for user " + userId, e);
        }
    }

    @Data
    private static class ImageSize {
        private final int width;
        private final int height;

        private ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public static ImageSize of(int width, int height) {
            return new ImageSize(width, height);
        }
    }
}
