package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository  extends JpaRepository<Project, Long> {
}
