package com.example.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5 MB


    public String storeImage(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        validate(file);

        try {
            Path targetDir = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String fileName = UUID.randomUUID() + extractExtension(file.getOriginalFilename());
            Path targetPath = targetDir.resolve(fileName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subfolder + "/" + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Could not store file, please try again", e);
        }
    }


    public void deleteImage(String publicPath) {
        if (publicPath == null || !publicPath.startsWith("/uploads/")) {
            return;
        }
        try {
            Path path = Paths.get(uploadDir, publicPath.substring("/uploads/".length()))
                    .toAbsolutePath().normalize();
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    private void validate(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image is too large (max 5MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported image type. Use JPG, PNG, WEBP or GIF");
        }
    }

    private String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            return ".jpg";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext) ? ext : ".jpg";
    }
}
