package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.service.project.milestone.MilestoneDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Value
public class ProjectGetByIdDto extends ProjectDto {
    Set<MilestoneDto> milestones;

    public ProjectGetByIdDto(Project project) {
        super(project);
        this.milestones = project.getMilestones().stream()
                .map(MilestoneDto::new)
                .collect(Collectors.toSet());
    }
}
