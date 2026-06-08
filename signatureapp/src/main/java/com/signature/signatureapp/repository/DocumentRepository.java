package com.signature.signatureapp.repository;

import com.signature.signatureapp.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository
        extends JpaRepository<Document,Long> {
}