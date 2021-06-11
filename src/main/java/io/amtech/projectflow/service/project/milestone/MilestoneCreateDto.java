package io.amtech.projectflow.service.project.milestone;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class MilestoneCreateDto {
    @NotBlank
    @Length(max = 255)
    String name;
    @Length(max = 2048)
    String description;
    long plannedStartDate;
    long plannedFinishDate;
    Long factStartDate;
    Long factFinishDate;
    short progressPercent;
}
