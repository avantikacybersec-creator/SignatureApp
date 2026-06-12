package com.signature.signatureapp.controller;

import com.signature.signatureapp.service.SignatureService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(
            SignatureService signatureService) {

        this.signatureService = signatureService;
    }

    @PostMapping("/upload")
    public String uploadSignature(

            @RequestParam("file")
            MultipartFile file) {

        return signatureService
                .uploadSignature(file);
    }
}