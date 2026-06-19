package com.signature.signatureapp.controller;

import com.signature.signatureapp.dto.SignaturePositionRequest;
import com.signature.signatureapp.model.Document;
import com.signature.signatureapp.repository.DocumentRepository;
import com.signature.signatureapp.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentRepository documentRepository;

    public DocumentController(
            DocumentService documentService,
            DocumentRepository documentRepository) {

        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    private final DocumentService documentService;



    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file) {

        return documentService.upload(file);
    }
    @GetMapping
    public List<Document> getMyDocuments() {

        return documentService.getMyDocuments();
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long id) {

        Resource file =
                documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + file.getFilename()
                                + "\""
                )
                .body(file);
    }
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long id) throws Exception {

        Document document =
                documentRepository.findById(id)
                        .get();

        System.out.println(
                "FILE PATH = " +
                        document.getFilePath()
        );

        Path path =
                Paths.get(document.getFilePath());

        System.out.println(
                "EXISTS = " +
                        Files.exists(path)
        );

        Resource resource =
                new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    @PostMapping("/sign/{id}")
    public String signDocument(
            @PathVariable Long id) {

        return documentService
                .signDocument(id);
    }
    @PostMapping("/apply-signature/{id}")
    public String applySignature(
            @PathVariable Long id,
            @RequestBody SignaturePositionRequest request) {

        return documentService.applySignature(
                id,
                request.getX(),
                request.getY()
        );
    }
}