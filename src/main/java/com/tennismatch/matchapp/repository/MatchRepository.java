package com.tennismatch.matchapp.repository;

import com.tennismatch.matchapp.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByPlayProposalId(Long proposalId);
} 