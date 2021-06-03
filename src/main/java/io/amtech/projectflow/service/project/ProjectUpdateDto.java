package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.project.ProjectStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectUpdateDto extends ProjectCreateDto {
    @NotNull
    private ProjectStatus projectStatus;
}
