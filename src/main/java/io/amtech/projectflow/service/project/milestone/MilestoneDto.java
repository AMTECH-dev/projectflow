package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import java.time.Instant;

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
        this.plannedStartDate = milestone.getPlannedStartDate().toEpochMilli();
        this.plannedFinishDate = milestone.getPlannedFinishDate().toEpochMilli();
        this.factStartDate = milestone.getFactStartDate().toEpochMilli();
        this.factFinishDate = milestone.getFactFinishDate().toEpochMilli();
        this.progressPercent = milestone.getProgressPercent();
    }
}
