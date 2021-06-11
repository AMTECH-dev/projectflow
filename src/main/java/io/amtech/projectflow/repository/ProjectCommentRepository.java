package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.project.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long>, ProjectCommentCustomRepository {
}
