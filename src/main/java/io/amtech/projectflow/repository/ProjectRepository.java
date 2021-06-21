package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    default Project findProjectByIdOrThrow(long projectId) {
        return findById(projectId)
                .orElseThrow(() -> new ObjectNotFoundException(Project.class.getSimpleName(), projectId));
    }
}
