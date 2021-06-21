package io.amtech.projectflow.service.direction;

import io.amtech.projectflow.domain.employee.Employee;
import jdk.jfr.Name;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class DirectionCreateDto {

    @NotNull
    @Name("lead_id")
    Long lead_id;

    @Length(max=255)
    @NotNull
    String name;
}
