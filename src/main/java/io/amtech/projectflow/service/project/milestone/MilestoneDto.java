package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import static io.amtech.projectflow.util.DateUtil.instantToMillis;

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
        this.plannedStartDate = instantToMillis(milestone.getPlannedStartDate());
        this.plannedFinishDate = instantToMillis(milestone.getPlannedFinishDate());
        this.factStartDate = instantToMillis(milestone.getFactStartDate());
        this.factFinishDate = instantToMillis(milestone.getFactFinishDate());
        this.progressPercent = milestone.getProgressPercent();
    }
}
