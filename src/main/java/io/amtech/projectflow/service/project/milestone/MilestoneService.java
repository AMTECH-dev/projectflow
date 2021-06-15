package io.amtech.projectflow.service.project.milestone;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface MilestoneService {
    MilestoneDto create(long projectId, MilestoneCreateDto createDto);
    MilestoneDto get(long projectId, long milestoneId);
    void update(long projectId, long milestoneId, MilestoneUpdateDto newData);
    void updateProgressPercent(long projectId, long milestoneId, MilestoneUpdateProgressDto updateProgressDto);
    void delete(long projectId, long milestoneId);
    PagedData<MilestoneDto> search(long projectId, SearchCriteria criteria);
}
