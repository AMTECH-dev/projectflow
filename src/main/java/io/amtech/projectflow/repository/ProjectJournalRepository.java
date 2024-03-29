package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.project.ProjectJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJournalRepository extends JpaRepository<ProjectJournal, Long>, ProjectJournalCustomRepository {
    default ProjectJournal findByIdOrThrow(long id) {
        return findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ProjectJournal.class.getSimpleName(), id));
    }
}
