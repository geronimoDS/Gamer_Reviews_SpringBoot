package com.gamer.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

//@Service
@Service("userFileStorageService")
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/users";

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + (extension != null ? "." + extension : "");
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);
        return fileName;
    }

    private String getFileExtension(String filename) {
        if (filename == null) return null;
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : null;
    }

    public boolean deleteFile(String fileName) {
        if (fileName == null) return false;
        File file = new File(UPLOAD_DIR, fileName);
        return file.exists() && file.delete();
    }
}
