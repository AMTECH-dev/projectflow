package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;

public interface DirectionCustomRepository {
    PagedData<Direction> search(SearchCriteria criteria);
}
