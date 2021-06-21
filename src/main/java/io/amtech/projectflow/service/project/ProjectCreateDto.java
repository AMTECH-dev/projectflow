package io.amtech.projectflow.service.project;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProjectCreateDto {
    @NotBlank
    @Length(max = 255)
    String name;
    @NotNull
    Long projectLeadId;
    @NotNull
    Long directionId;
    @Length(max = 2048)
    String description;
}
