package io.amtech.projectflow.service.project;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface ProjectService {
    ProjectDto create(ProjectCreateDto projectCreateDto);

    ProjectGetByIdDto get(long id);

    void update(long id, ProjectUpdateDto projectUpdateDto);

    void delete(long id);

    PagedData<ProjectDto> search(SearchCriteria criteria);
}
