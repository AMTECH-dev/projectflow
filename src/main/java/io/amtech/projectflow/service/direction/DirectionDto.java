package io.amtech.projectflow.service.direction;

import io.amtech.projectflow.domain.Direction;
import lombok.Value;

@Value
public class DirectionDto {
    long id;
    long leadId;
    String name;
    String leadName;

    public DirectionDto(final Direction direction) {
        this.id = direction.getId();
        this.name = direction.getName();
        this.leadId = direction.getLead().getId();
        this.leadName = direction.getLead().getName();
    }
}