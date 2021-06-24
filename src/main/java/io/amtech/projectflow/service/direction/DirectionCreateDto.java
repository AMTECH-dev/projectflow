package io.amtech.projectflow.service.direction;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DirectionCreateDto {
    @NotNull
    Long leadId;

    @Length(max = 255)
    @NotBlank
    String name;
}
