package io.amtech.projectflow.service.audit;

import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import lombok.Value;

import java.time.Instant;
import java.util.Set;

@Value
public class AuditProject {
    long id;
    String name;
    Instant createDate;
    long projectLeadId;
    long directionId;
    ProjectStatus status;
    Set<Milestone> milestones;

    public AuditProject(final Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.createDate = project.getCreateDate();
        this.projectLeadId = project.getProjectLead().getId();
        this.directionId = project.getDirection().getId();
        this.status = project.getProjectStatus();
        this.milestones = project.getMilestones();
    }
}
