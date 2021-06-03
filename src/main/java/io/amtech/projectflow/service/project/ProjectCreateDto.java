package io.amtech.projectflow.service.project;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;

@Data
public class ProjectCreateDto {
    @NotBlank
    @Length(max = 255)
    private String name;
    @NotNull
    private Employee projectLead;
    @NotNull
    private Direction direction;
    @Length(max = 2048)
    private String description;
    @NotNull
    private Instant createDate;
}
