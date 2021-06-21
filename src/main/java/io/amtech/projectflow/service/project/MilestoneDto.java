package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.project.Milestone;
import lombok.Value;

import static io.amtech.projectflow.util.ConvertingUtil.instantToSecond;

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
        this.plannedStartDate = instantToSecond(milestone.getPlannedStartDate());
        this.plannedFinishDate = instantToSecond(milestone.getPlannedFinishDate());
        this.factStartDate = instantToSecond(milestone.getFactStartDate());
        this.factFinishDate = instantToSecond(milestone.getFactFinishDate());
        this.progressPercent = milestone.getProgressPercent();
    }
}
