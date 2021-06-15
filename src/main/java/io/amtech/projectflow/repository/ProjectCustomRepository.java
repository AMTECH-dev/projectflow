package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Project;

public interface ProjectCustomRepository {
    PagedData<Project> search(SearchCriteria criteria);
}
