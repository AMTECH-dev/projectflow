package io.amtech.projectflow.service.project_journal.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.repository.ProjectJournalRepository;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.project_journal.ProjectJournalDto;
import io.amtech.projectflow.service.project_journal.ProjectJournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectJournalServiceImpl implements ProjectJournalService {
    private final ProjectRepository projectRepository;
    private final ProjectJournalRepository projectJournalRepository;

    @Override
    public PagedData<ProjectJournalDto> search(long projectId, SearchCriteria criteria) {
        checkProjectByIdOnExists(projectId);
        return projectJournalRepository.search(projectId, criteria)
                .map(ProjectJournalDto::new);
    }

    @Override
    public ProjectJournalDto get(long projectId, long id) {
        checkProjectByIdOnExists(projectId);
        return new ProjectJournalDto(findProjectJournalByIdOrThrow(id));
    }

    private void checkProjectByIdOnExists(long id) {
        projectRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Project.class.getSimpleName(), id));
    }

    private ProjectJournal findProjectJournalByIdOrThrow(long id) {
        return projectJournalRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ProjectJournal.class.getSimpleName(), id));
    }
}
