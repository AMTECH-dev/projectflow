package io.amtech.projectflow.service.project_comment.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectComment;
import io.amtech.projectflow.repository.ProjectCommentRepository;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.project_comment.ProjectCommentCreateDto;
import io.amtech.projectflow.service.project_comment.ProjectCommentDto;
import io.amtech.projectflow.service.project_comment.ProjectCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommentServiceImpl implements ProjectCommentService {
    private static final String OBJ_DESC = "ProjectComment";

    private final ProjectCommentRepository projectCommentRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ProjectCommentDto create(long projectId, ProjectCommentCreateDto createDto) {
        Project p = findProjectByIdOrThrow(projectId);

        ProjectComment pc = new ProjectComment()
                .setCreateDate(Instant.now())
                .setMessage(createDto.getMessage())
                .setLogin(p.getProjectLead().getName());
        p.getProjectComments().add(pc);
        projectCommentRepository.save(pc);

        return new ProjectCommentDto(pc);
    }

    @Override
    public ProjectCommentDto get(long projectId, long id) {
        findProjectByIdOrThrow(projectId);

        return new ProjectCommentDto(findByIdOrThrow(id));
    }

    @Override
    public void update(long projectId, long id, ProjectCommentCreateDto newData) {
        findProjectByIdOrThrow(projectId);

        ProjectComment pc = findByIdOrThrow(id);
        pc.setMessage(newData.getMessage());
    }

    @Override
    public void delete(long projectId, long id) {
        findProjectByIdOrThrow(projectId);
        findByIdOrThrow(id);
        projectCommentRepository.deleteById(id);
    }

    @Override
    public PagedData<ProjectCommentDto> search(long projectId, SearchCriteria criteria) {
        findProjectByIdOrThrow(projectId);
        return projectCommentRepository.search(projectId, criteria).map(ProjectCommentDto::new);
    }

    private Project findProjectByIdOrThrow(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ObjectNotFoundException("Project", projectId));
    }

    private ProjectComment findByIdOrThrow(long id) {
        return projectCommentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJ_DESC, id));
    }
}
