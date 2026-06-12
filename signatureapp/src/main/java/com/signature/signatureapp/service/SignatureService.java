package com.signature.signatureapp.service;

import com.signature.signatureapp.model.Signature;
import com.signature.signatureapp.model.User;
import com.signature.signatureapp.repository.SignatureRepository;
import com.signature.signatureapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;

@Service
public class SignatureService {

    private final SignatureRepository repository;
    private final UserRepository userRepository;

    public SignatureService(
            SignatureRepository repository,
            UserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    public String uploadSignature(MultipartFile file) {

        try {

            String email =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getName();

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow();

            Path uploadPath =
                    Paths.get("uploads/signatures");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName =
                    user.getId() + "_" +
                            file.getOriginalFilename();

            Path filePath =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Signature signature =
                    repository
                            .findByUser(user)
                            .orElse(new Signature());

            signature.setUser(user);
            signature.setImagePath(
                    filePath.toString()
            );

            repository.save(signature);

            return "Signature Uploaded Successfully";

        } catch (Exception e) {

            throw new RuntimeException(
                    e.getMessage()
            );
        }
    }
}