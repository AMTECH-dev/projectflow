package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.project.ProjectJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJournalRepository extends JpaRepository<ProjectJournal, Long> {
}
