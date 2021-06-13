package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.util.DateUtil;
import lombok.Value;

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
        this.factStartDate = DateUtil.instantToMillis(milestone.getFactStartDate());
        this.factFinishDate = DateUtil.instantToMillis(milestone.getFactFinishDate());
        this.progressPercent = milestone.getProgressPercent();
    }
}
