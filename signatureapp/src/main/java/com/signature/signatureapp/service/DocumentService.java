package com.signature.signatureapp.service;

import com.signature.signatureapp.model.Document;
import com.signature.signatureapp.repository.DocumentRepository;
import com.signature.signatureapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public String upload(MultipartFile file) {

        try {

            String fileName = file.getOriginalFilename();

            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Document document = new Document();

            document.setFileName(fileName);
            document.setFilePath(filePath.toString());
            document.setUploadedAt(LocalDateTime.now());

            repository.save(document);

            return "File Uploaded Successfully";

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
    }
}