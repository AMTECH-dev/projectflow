package io.amtech.projectflow.service.project_comment;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ProjectCommentCreateDto {
    @NotBlank
    @Length(max = 5000)
    private String message;
}
