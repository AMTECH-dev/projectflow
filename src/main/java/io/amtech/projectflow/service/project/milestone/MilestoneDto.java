package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import static io.amtech.projectflow.util.ConvertingUtil.instantToSecond;

@Value
public class MilestoneDto {
    long id;
    String name;
    String description;
    Long plannedStartDate;
    Long plannedFinishDate;
    Long factStartDate;
    Long factFinishDate;
    short progressPercent;

    public MilestoneDto(final Milestone milestone) {
        this.id = milestone.getId();
        this.name = milestone.getName();
        this.description = milestone.getDescription();
        this.plannedStartDate = instantToSecond(milestone.getPlannedStartDate());
        this.plannedFinishDate = instantToSecond(milestone.getPlannedFinishDate());
        this.factStartDate = instantToSecond(milestone.getFactStartDate());
        this.factFinishDate = instantToSecond(milestone.getFactFinishDate());
        this.progressPercent = milestone.getProgressPercent();
    }
}
