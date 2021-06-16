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
    long projectLeadId;
    long directionId;
    String description;
    long createDate;
    ProjectStatus projectStatus;


    public ProjectDto(final Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.projectLeadId = project.getProjectLead().getId();
        this.directionId = project.getDirection().getId();
        this.description = project.getDescription();
        this.createDate = project.getCreateDate().toEpochMilli();
        this.projectStatus = project.getProjectStatus();

    }
}
