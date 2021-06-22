package io.amtech.projectflow.service.direction;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import lombok.Value;

@Value
public class DirectionDto {
    long id;
    long leadId;
    String name;

    public DirectionDto(final Direction direction) {
        this.id=direction.getId();
        this.leadId=direction.getLead().getId();
        this.name=direction.getName();
    }

}