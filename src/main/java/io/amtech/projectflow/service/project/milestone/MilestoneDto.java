package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import java.time.Instant;

@Value
public class MilestoneDto {
    long id;
    String name;
    String description;
    Instant plannedStartDate;
    Instant plannedFinishDate;
    Instant factStartDate;
    Instant factFinishDate;
    short progressPercent;

    public MilestoneDto(final Milestone milestone) {
        this.id = milestone.getId();
        this.name = milestone.getName();
        this.description = milestone.getDescription();
        this.plannedStartDate = milestone.getPlannedStartDate();
        this.plannedFinishDate = milestone.getPlannedFinishDate();
        this.factStartDate = milestone.getFactStartDate();
        this.factFinishDate = milestone.getFactFinishDate();
        this.progressPercent = milestone.getProgressPercent();
    }
}
