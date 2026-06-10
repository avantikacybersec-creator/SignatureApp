package com.signature.signatureapp.controller;

import com.signature.signatureapp.model.Document;
import com.signature.signatureapp.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

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
}