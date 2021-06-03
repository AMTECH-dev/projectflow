package io.amtech.projectflow.service.employee;

import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.UserPosition;
import lombok.Value;

@Value
public class EmployeeDto {
    private long id;
    private String name;
    private String email;
    private String phone;
    private UserPosition position;
    private boolean isFired;

    public EmployeeDto(final Employee employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.email = employee.getEmail();
        this.phone = employee.getPhone();
        this.position = employee.getPosition();
        this.isFired = employee.isFired();
    }
}
