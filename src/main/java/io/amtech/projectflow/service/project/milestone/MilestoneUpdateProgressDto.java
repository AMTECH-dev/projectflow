package io.amtech.projectflow.service.project.milestone;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class MilestoneUpdateProgressDto {
    @Range(min = 0, max = 100)
    private short progressPercent = 0;
}
