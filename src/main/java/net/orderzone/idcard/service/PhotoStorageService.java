package net.orderzone.idcard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoStorageService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

    @Value("${app.photo.dir}")
    private String photoDir;

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("No file provided");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Only JPEG/PNG allowed");
        if (file.getSize() > MAX_SIZE)
            throw new IllegalArgumentException("File exceeds 5 MB limit");

        Path dir = Paths.get(photoDir);
        Files.createDirectories(dir);

        String ext = file.getOriginalFilename() != null
                && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String filename = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public void delete(String filename) throws IOException {
        if (filename == null) return;
        Path p = Paths.get(photoDir, filename);
        Files.deleteIfExists(p);
    }

    public Path resolve(String filename) {
        return Paths.get(photoDir, filename);
    }
}