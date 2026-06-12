package com.signature.signatureapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}