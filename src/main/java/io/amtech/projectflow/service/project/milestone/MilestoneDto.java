package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import static io.amtech.projectflow.util.DateUtil.instantToSeconds;

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
        this.plannedStartDate = instantToSeconds(milestone.getPlannedStartDate());
        this.plannedFinishDate = instantToSeconds(milestone.getPlannedFinishDate());
        this.factStartDate = instantToSeconds(milestone.getFactStartDate());
        this.factFinishDate = instantToSeconds(milestone.getFactFinishDate());
        this.progressPercent = milestone.getProgressPercent();
    }
}
