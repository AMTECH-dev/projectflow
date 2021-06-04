package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import lombok.Value;

import java.time.Instant;

@Value
public class ProjectDto {
    long id;
    String name;
    Employee projectLead;
    Direction direction;
    String description;
    Instant createDate;
    ProjectStatus projectStatus;

    public ProjectDto(final Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.projectLead = project.getProjectLead();
        this.direction = project.getDirection();
        this.description = project.getDescription();
        this.createDate = project.getCreateDate();
        this.projectStatus = project.getProjectStatus();
    }
}
