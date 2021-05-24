package io.amtech.projectflow.service.employee;

import io.amtech.projectflow.domain.employee.UserPosition;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static io.amtech.projectflow.util.ValidationUtil.EMAIL_REGEXP;

@Data
public class EmployeeCreateDto {
    @NotBlank
    @Length(max = 255)
    private String name;
    @Pattern(regexp = EMAIL_REGEXP, message = "Invalid email format")
    private String email;
    @Length(max = 50)
    private String phone;
    @NotNull
    private UserPosition position;
}
