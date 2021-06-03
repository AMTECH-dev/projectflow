package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class ProjectDto {
    private long id;
    private String name;
    private Employee projectLead;
    private Direction direction;
    private String description;
    private Timestamp createDate;
    private ProjectStatus projectStatus;

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
