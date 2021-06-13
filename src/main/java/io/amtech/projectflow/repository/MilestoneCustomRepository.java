package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Milestone;

public interface MilestoneCustomRepository {
    PagedData<Milestone> search(long projectId, SearchCriteria criteria);
}
