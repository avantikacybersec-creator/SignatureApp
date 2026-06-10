package com.signature.signatureapp.service;

import com.signature.signatureapp.model.Document;
import com.signature.signatureapp.model.User;
import com.signature.signatureapp.repository.DocumentRepository;
import com.signature.signatureapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final UserRepository userRepository;
    public DocumentService(DocumentRepository repository,
                           UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }
    public List<Document> getMyDocuments() {

        System.out.println(
                "AUTH = " +
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
        );

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        return repository.findByUser(user);
    }

    public String upload(MultipartFile file) {

        System.out.println(
                "AUTH = " +
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
        );

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

            String email =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getName();

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow();

            document.setUser(user);

            repository.save(document);

            return "File Uploaded Successfully";

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
    }
    public Resource downloadDocument(Long id) {

        try {

            Document document =
                    repository.findById(id)
                            .orElseThrow();

            Path path =
                    Paths.get(document.getFilePath());

            return new UrlResource(
                    path.toUri()
            );

        } catch (MalformedURLException e) {

            throw new RuntimeException(
                    "File not found"
            );
        }
    }
}