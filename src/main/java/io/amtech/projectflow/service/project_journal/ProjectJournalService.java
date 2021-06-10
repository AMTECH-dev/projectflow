package io.amtech.projectflow.service.project_journal;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface ProjectJournalService {
    PagedData<ProjectJournalDto> search(long projectId, final SearchCriteria criteria);
    ProjectJournalDto get(long projectId, long id);
}
