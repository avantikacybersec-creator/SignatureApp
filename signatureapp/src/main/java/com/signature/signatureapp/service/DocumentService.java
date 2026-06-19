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
    private final AuditService auditService;
    public DocumentService(
            DocumentRepository repository,
            UserRepository userRepository,
            SignatureRepository signatureRepository,
            AuditService auditService) {

        this.repository = repository;
        this.userRepository = userRepository;
        this.signatureRepository = signatureRepository;
        this.auditService = auditService;
    }
    public List<Document> getMyDocuments() {

        String email = getCurrentUserEmail();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Not found"));

        return repository.findByUser(user);
    }

    public String upload(MultipartFile file) {

        try {

            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.trim().isEmpty()) {
                throw new RuntimeException("Invalid file name");
            }

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

            String email = getCurrentUserEmail();

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Not found"));

            Document document = new Document();

            document.setFileName(fileName);
            document.setFilePath(filePath.toString());
            document.setUploadedAt(LocalDateTime.now());
            document.setSigned(false);
            document.setUser(user);

            repository.save(document);

            auditService.log(
                    email,
                    "UPLOAD",
                    document.getFileName()
            );

            return "File Uploaded Successfully";

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
    }
    public Resource downloadDocument(Long id) {

        try {

            Document document =
                    repository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Not found"));

            Path path =
                    Paths.get(document.getFilePath());
            System.out.println("Path: " + path);
            System.out.println("Exists: " + Files.exists(path));
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
                        .orElseThrow(() -> new RuntimeException("Not found"));

        document.setSigned(true);

        document.setSignedAt(
                LocalDateTime.now()
        );

        repository.save(document);

        String email = getCurrentUserEmail();

        auditService.log(
                email,
                "SIGN",
                document.getFileName()
        );

        return "Document Signed Successfully";
    }
    public String applySignature(
            Long id,
            float x,
            float y) {

        try {

            String email = getCurrentUserEmail();

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Not found"));

            Document document =
                    repository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Not found"));

            com.signature.signatureapp.model.Signature signature =
                    signatureRepository
                            .findByUser(user)
                            .orElseThrow(() -> new RuntimeException("Not found"));

            try (PDDocument pdf =
                         Loader.loadPDF(
                                 new java.io.File(
                                         document.getFilePath()
                                 ))) {

                System.out.println(
                        "PDF Loaded: " +
                                document.getFilePath()
                );

                System.out.println(
                        "Pages: " +
                                pdf.getNumberOfPages()
                );


                PDPage page =
                        pdf.getPage(0);

                float pdfHeight =
                        page.getMediaBox().getHeight();

                float pdfY =
                        pdfHeight - y - 60;

                PDImageXObject image =
                        PDImageXObject.createFromFile(
                                signature.getImagePath(),
                                pdf
                        );

                try (PDPageContentStream contentStream =
                             new PDPageContentStream(
                                     pdf,
                                     page,
                                     PDPageContentStream.AppendMode.APPEND,
                                     true
                             )) {

                    contentStream.drawImage(
                            image,
                            x,
                            pdfY,
                            120,
                            60
                    );
                }

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

                document.setFilePath(
                        signedPath.toString()
                );

                document.setSigned(true);
                document.setSignedAt(LocalDateTime.now());

                repository.save(document);
                auditService.log(
                        user.getEmail(),
                        "APPLY_SIGNATURE",
                        document.getFileName()
                );

                return "Document Signed And Saved Successfully";


            }
        } catch (Exception e) {

            throw new RuntimeException(
                    e.getMessage()
            );
        }

    }
    private String getCurrentUserEmail() {

        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            throw new RuntimeException(
                    "User not authenticated"
            );
        }

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}