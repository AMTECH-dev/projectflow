package io.amtech.projectflow.service.project.milestone;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.Instant;

@Data
public class MilestoneCreateDto {
    @NotBlank
    @Length(max = 255)
    String name;
    @Length(max = 2048)
    String description;
    @NotNull
    Instant plannedStartDate;
    @NotNull
    Instant plannedFinishDate;
    Instant factStartDate;
    Instant factFinishDate;
    @NotNull
    short progressPercent;
}
