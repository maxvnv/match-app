package com.tennismatch.matchapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(max = 120) // Storing hashed password, so size should be adequate
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NtrpLevel ntrpLevel;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String homeTown;

    // Optional fields
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @ElementCollection(fetch = FetchType.EAGER) // Eager fetch for roles as they are core to user identity
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default // Initialize with an empty set if not provided by builder
    private Set<String> roles = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationships (e.g., with PlayProposal, Match) will be added later
} 