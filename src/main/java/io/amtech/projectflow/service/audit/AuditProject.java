package io.amtech.projectflow.service.audit;

import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import lombok.Value;

import java.time.Instant;
import java.util.Set;

@Value
public class AuditProject {
    String name;
    long leadId;
    long directionId;
    Instant createDate;
    ProjectStatus status;
    Set<Milestone> milestones;

    public AuditProject(final Project p) {
        this.name = p.getName();
        this.leadId = p.getProjectLead().getId();
        this.directionId = p.getDirection().getId();
        this.createDate = p.getCreateDate();
        this.status = p.getProjectStatus();
        this.milestones = p.getMilestones();
    }
}
