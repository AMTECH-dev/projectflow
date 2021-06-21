package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.project.Project;
import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;

@Value
public class ProjectGetByIdDto extends ProjectDto {
    Set<MilestoneDto> milestones;

    //Берем у проекта вехи-переводим в стрим-
    public ProjectGetByIdDto(Project project) {
        super(project);
        this.milestones = project.getMilestones().stream()
                .map(MilestoneDto::new)
                .collect(Collectors.toSet());
    }
}
