package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.project.Project;

public interface DirectionCustomRepository {
    PagedData<Direction> search(SearchCriteria criteria);
}
