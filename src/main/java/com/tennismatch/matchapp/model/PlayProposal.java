package com.tennismatch.matchapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "play_proposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User proposingUser;

    @Column(nullable = false)
    private LocalDateTime proposedStartTime;

    private LocalDateTime proposedEndTime; // Optional, could be a duration instead

    @Column(nullable = false)
    private String locationDetails; // E.g., "City Park Tennis Court 3" or "Any court in Downtown area"

    @Lob // For potentially longer text
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "playProposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Match confirmedMatch;

    // Relationships (e.g., with Match requests or confirmed Match) will be added later
} 