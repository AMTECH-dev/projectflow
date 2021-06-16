package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

@Value
public class MilestoneDto {

    long id;
    String name;
    String description;
    long plannedStartDate;
    long plannedFinishDate;
    long factStartDate;
    long factFinishDate;
    short progressPercent;

    public MilestoneDto(Milestone milestone) {
        this.id = milestone.getId();
        this.name = milestone.getName();
        this.description = milestone.getDescription();
        this.plannedStartDate = milestone.getPlannedStartDate().getEpochSecond();
        this.plannedFinishDate = milestone.getPlannedFinishDate().getEpochSecond();
        this.factStartDate = milestone.getFactStartDate().getEpochSecond();
        this.factFinishDate = milestone.getFactFinishDate().getEpochSecond();
        this.progressPercent = milestone.getProgressPercent();
    }
}
