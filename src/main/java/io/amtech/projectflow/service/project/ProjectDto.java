package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.project.*;
import lombok.Value;

import java.time.Instant;
import java.util.Set;

@Value
public class ProjectDto {

    Long id;
    String name;
    Employee projectLead;
    Direction direction;
    String description;
    long createDate;
    ProjectStatus projectStatus;
    Set<Milestone> milestones;
    Set<ProjectComment> projectComments;
    Set<ProjectJournal> history;

    public ProjectDto(final Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.projectLead = project.getProjectLead();
        this.direction = project.getDirection();
        this.description = project.getDescription();
        this.createDate = project.getCreateDate().toEpochMilli();
        this.projectStatus = project.getProjectStatus();
        this.milestones = project.getMilestones();
        this.projectComments = project.getProjectComments();
        this.history = project.getHistory();
    }
}
