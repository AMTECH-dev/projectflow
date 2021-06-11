package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import java.time.Instant;
import java.util.Optional;

@Value
public class MilestoneDto {
    long id;
    String name;
    String description;
    long plannedStartDate;
    long plannedFinishDate;
    Long factStartDate;
    Long factFinishDate;
    short progressPercent;

    public MilestoneDto(final Milestone milestone) {
        this.id = milestone.getId();
        this.name = milestone.getName();
        this.description = milestone.getDescription();
        this.plannedStartDate = milestone.getPlannedStartDate().toEpochMilli();
        this.plannedFinishDate = milestone.getPlannedFinishDate().toEpochMilli();
        this.factStartDate = Optional.ofNullable(milestone.getFactStartDate())
                .map(Instant::toEpochMilli)
                .orElse(null);
        this.factFinishDate = Optional.ofNullable(milestone.getFactFinishDate())
                .map(Instant::toEpochMilli)
                .orElse(null);
        this.progressPercent = milestone.getProgressPercent();
    }
}
