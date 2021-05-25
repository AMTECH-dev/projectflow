package io.amtech.projectflow.service.employee;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeUpdateDto extends EmployeeCreateDto {
    @NotNull
    private Boolean isFired;
}
