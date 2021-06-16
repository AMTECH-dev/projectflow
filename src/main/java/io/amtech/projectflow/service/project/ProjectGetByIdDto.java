package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Project;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public class ProjectGetByIdDto extends ProjectDto{
    Set<MilestoneDto> milestones ;


//Берем у проекта вехи-переводим в стрим-
    public ProjectGetByIdDto(Project project) {
        super(project);
        this.milestones = project.getMilestones().stream().map(new Function<Milestone, MilestoneDto>() {

            @Override
            public MilestoneDto apply(Milestone milestone) {
                return new MilestoneDto(milestone);
            }
        }).collect(Collectors.toSet());
    }
}
