package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.ProjectComment;

public interface ProjectCommentCustomRepository {
    PagedData<ProjectComment> search(long projectId, SearchCriteria criteria);
}
