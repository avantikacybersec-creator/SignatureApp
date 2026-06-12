package com.signature.signatureapp.repository;

import com.signature.signatureapp.model.Signature;
import com.signature.signatureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureRepository
        extends JpaRepository<Signature, Long> {

    Optional<Signature> findByUser(User user);
}