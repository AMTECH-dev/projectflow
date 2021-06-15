package io.amtech.projectflow.service.project.milestone;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MilestoneUpdateProgressDto {
    @NotNull
    Short progressPercent;
}
