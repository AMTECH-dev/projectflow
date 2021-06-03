package io.amtech.projectflow.service.project.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.project.ProjectCreateDto;
import io.amtech.projectflow.service.project.ProjectDto;
import io.amtech.projectflow.service.project.ProjectService;
import io.amtech.projectflow.service.project.ProjectUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private static final String OBJ_DESC = "Project";
    private final ProjectRepository projectRepository;

    @Override
    public ProjectDto create(ProjectCreateDto createDto) {
        Project p = new Project()
                .setName(createDto.getName())
                .setProjectLead(createDto.getProjectLead())
                .setDirection(createDto.getDirection())
                .setDescription(createDto.getDescription())
                .setCreateDate(createDto.getCreateDate())
                .setProjectStatus(ProjectStatus.UNAPPROVED); // проверить корректность первоначального статуса
        projectRepository.save(p);

        return new ProjectDto(p);
    }

    @Override
    public ProjectDto get(long id) {
        return new ProjectDto(findByIdOrThrow(id));
    }

    private Project findByIdOrThrow(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJ_DESC, id));
    }

    @Override
    public void update(long id, ProjectUpdateDto newData) {
        Project p = findByIdOrThrow(id);
        p.setName(newData.getName());
        p.setProjectLead(newData.getProjectLead());
        p.setDirection(newData.getDirection());
        p.setDescription(newData.getDescription());
//        p.setCreateDate(newData.getCreateDate());
        p.setProjectStatus(newData.getProjectStatus());
    }

//    @Override
//    public void delete(long id) {
//        findByIdOrThrow(id);
//
//        projectRepository.deleteById(id);
//    }
}
