package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.project.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long>, MilestoneCustomRepository {
}
