package com.gamer.api.storage;
// 3) Servicio de almacenamiento de archivos

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class FileStorageService {

    private final Path root = Path.of("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear carpeta uploads", e);
        }
    }

    public String saveImage(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) return null;

            List<String> tipos = List.of("image/jpeg", "image/png", "image/webp", "image/jpg");
            if (!tipos.contains(file.getContentType())) {
                throw new IllegalArgumentException("Tipo de archivo no permitido");
            }

            Path targetDir = root.resolve(folder);
            Files.createDirectories(targetDir);

            String original = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
            String fileName = UUID.randomUUID().toString() + "_" + original;
            Path target = targetDir.resolve(fileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // devolver ruta relativa accesible, por ejemplo: /uploads/games/uuid_name.jpg
            return "/uploads/" + folder + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Error guardando imagen", ex);
        }
    }

    public boolean deleteImageByUrl(String imagenUrl) {
        if (imagenUrl == null || imagenUrl.isBlank()) return false;
        try {
            // imagenUrl: /uploads/games/uuid_name.jpg
            String rel = imagenUrl.replaceFirst("^/+", "");
            Path p = Path.of(rel);
            return Files.deleteIfExists(p);
        } catch (Exception e) {
            return false;
        }
    }
}
