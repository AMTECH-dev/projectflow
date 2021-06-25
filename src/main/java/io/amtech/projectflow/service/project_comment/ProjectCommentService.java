package io.amtech.projectflow.service.project_comment;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface ProjectCommentService {
    ProjectCommentDto create(long projectId, ProjectCommentCreateDto createDto);

    ProjectCommentDto get(long projectId, long id);

    void update(long projectId, long id, ProjectCommentCreateDto newData);

    void delete(long projectId, long id);

    PagedData<ProjectCommentDto> search(long projectId, SearchCriteria criteria);
}
