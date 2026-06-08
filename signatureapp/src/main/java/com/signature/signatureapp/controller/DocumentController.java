package com.signature.signatureapp.controller;

import com.signature.signatureapp.service.DocumentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}