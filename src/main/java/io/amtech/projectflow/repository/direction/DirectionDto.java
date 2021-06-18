package io.amtech.projectflow.repository.direction;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import lombok.Value;

@Value
public class DirectionDto {
    long id;
    Employee lead;
    String name;

    public DirectionDto(final Direction direction) {
        this.id=direction.getId();
        this.lead=direction.getLead();
        this.name=direction.getName();
    }

}