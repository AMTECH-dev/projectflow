package io.amtech.projectflow.service.project;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
public class ProjectCreateDto {

    @NotBlank
    @Length(max = 255)
    String name;

    long projectLeadId;

    long directionId;

    @NotBlank
    @Length(max = 2048)
    String description;

    long createDate;
}
