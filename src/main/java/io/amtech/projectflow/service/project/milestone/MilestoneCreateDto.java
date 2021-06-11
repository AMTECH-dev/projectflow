package io.amtech.projectflow.service.project.milestone;

import lombok.Data;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MilestoneCreateDto {
    @NotBlank
    @Length(max = 255)
    String name;
    @Length(max = 2048)
    String description;
    @NotNull
    Long plannedStartDate;
    @NotNull
    Long plannedFinishDate;
    Long factStartDate;
    Long factFinishDate;
    @NotNull
    Short progressPercent;
}
