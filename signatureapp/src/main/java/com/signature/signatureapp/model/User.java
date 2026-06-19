package com.signature.signatureapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Document> documents;
    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Signature signature;
    @Enumerated(EnumType.STRING)
    private Role role;

     public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}



