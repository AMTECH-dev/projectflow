package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.ProjectJournal;

public interface ProjectJournalCustomRepository {
    PagedData<ProjectJournal> search(long projectId, SearchCriteria criteria);
}
