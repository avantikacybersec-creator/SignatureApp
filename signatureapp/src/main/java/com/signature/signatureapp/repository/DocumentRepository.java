package com.signature.signatureapp.repository;

import com.signature.signatureapp.model.Document;
import com.signature.signatureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository
        extends JpaRepository<Document,Long> {
    List<Document> findByUser(User user);
}