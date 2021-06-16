package io.amtech.projectflow.service.project.milestone;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class MilestoneUpdateProgressDto {
    @NotNull
    @Range(min = 0, max = 100)
    private Short progressPercent;
}
