package io.amtech.projectflow.service.project.milestone;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MilestoneCreateDto {
    @NotBlank
    @Length(max = 255)
    private String name;
    @Length(max = 2048)
    private String description;
    @NotNull
    private Long plannedStartDate;
    @NotNull
    private Long plannedFinishDate;
    private Long factStartDate;
    private Long factFinishDate;
    @NotNull
    private Short progressPercent;
}
