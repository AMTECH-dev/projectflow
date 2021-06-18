package io.amtech.projectflow.repository.direction;

import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class DirectionCreateDto {

    //long id;
    Employee lead;

    @Length(max=255)
    @NotNull
    String name;
}
