package io.amtech.projectflow.service.project_comment;

import io.amtech.projectflow.domain.project.ProjectComment;
import lombok.Value;

import static io.amtech.projectflow.util.ConvertingUtil.instantToSecond;

@Value
public class ProjectCommentDto {
    long id;
    String message;
    long createDate;
    String author;

    public ProjectCommentDto(final ProjectComment comment) {
        this.id = comment.getId();
        this.message = comment.getMessage();
        this.createDate = instantToSecond(comment.getCreateDate());
        this.author = comment.getLogin();
    }
}
