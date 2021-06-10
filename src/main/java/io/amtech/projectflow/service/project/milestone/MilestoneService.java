package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface MilestoneService {
    MilestoneDto create(long projectId, MilestoneCreateDto createDto);
    MilestoneDto get(long id);
    void update(long id, MilestoneUpdateDto newData);
    void delete(long id);
    PagedData<MilestoneDto> search(SearchCriteria criteria);
}
