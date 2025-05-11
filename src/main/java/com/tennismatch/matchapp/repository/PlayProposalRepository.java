package com.tennismatch.matchapp.repository;

import com.tennismatch.matchapp.model.PlayProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayProposalRepository extends JpaRepository<PlayProposal, Long> {
    List<PlayProposal> findByProposingUserId(Long userId);
} 