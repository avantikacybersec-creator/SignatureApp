package com.signature.signatureapp.service;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.signature.signatureapp.model.Document;
import com.signature.signatureapp.model.User;
import com.signature.signatureapp.repository.DocumentRepository;
import com.signature.signatureapp.repository.SignatureRepository;
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

    private final SignatureRepository signatureRepository;
    private final DocumentRepository repository;
    private final UserRepository userRepository;
    public DocumentService(
            DocumentRepository repository,
            UserRepository userRepository,
            SignatureRepository signatureRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
        this.signatureRepository = signatureRepository;
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
            document.setSigned(false);
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
    public String signDocument(Long id) {

        Document document =
                repository.findById(id)
                        .orElseThrow();

        document.setSigned(true);

        document.setSignedAt(
                LocalDateTime.now()
        );

        repository.save(document);

        return "Document Signed Successfully";
    }
    public String applySignature(Long id) {

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

            Document document =
                    repository.findById(id)
                            .orElseThrow();

            com.signature.signatureapp.model.Signature signature =
                    signatureRepository
                            .findByUser(user)
                            .orElseThrow();

            PDDocument pdf =
                    Loader.loadPDF(
                            new java.io.File(
                                    document.getFilePath()
                            )
                    );

            PDPage page =
                    pdf.getPage(0);

            PDImageXObject image =
                    PDImageXObject.createFromFile(
                            signature.getImagePath(),
                            pdf
                    );

            PDPageContentStream contentStream =
                    new PDPageContentStream(
                            pdf,
                            page,
                            PDPageContentStream.AppendMode.APPEND,
                            true
                    );

            contentStream.drawImage(
                    image,
                    400,   // X Position
                    100,   // Y Position
                    120,   // Width
                    60     // Height
            );

            contentStream.close();

            Path signedFolder =
                    Paths.get("uploads/signed");

            if (!Files.exists(signedFolder)) {
                Files.createDirectories(signedFolder);
            }

            String signedFileName =
                    "signed_" + document.getFileName();

            Path signedPath =
                    signedFolder.resolve(
                            signedFileName
                    );

            pdf.save(
                    signedPath.toFile()
            );

            pdf.close();

            document.setSigned(true);
            document.setSignedAt(LocalDateTime.now());

            repository.save(document);

            return "Document Signed And Saved Successfully";

        } catch (Exception e) {

            throw new RuntimeException(
                    e.getMessage()
            );
        }
    }
}