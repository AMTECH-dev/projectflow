package io.amtech.projectflow.service.audit;

import io.amtech.projectflow.domain.project.Project;

public interface AuditService {
    void save(String username, Project project);
}