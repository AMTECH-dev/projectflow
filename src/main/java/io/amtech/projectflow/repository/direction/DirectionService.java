package io.amtech.projectflow.repository.direction;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface DirectionService {

    DirectionDto create(DirectionCreateDto createDto);
    DirectionDto get(long id);
    void update(long id, DirectionUpdateDto newData);
    void delete(long id);
    PagedData<DirectionDto> search(SearchCriteria criteria);
}
