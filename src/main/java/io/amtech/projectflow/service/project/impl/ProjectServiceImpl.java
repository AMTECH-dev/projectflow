package io.amtech.projectflow.service.project.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.project.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private static final String OBJ_DESC = "Project";
    private final ProjectRepository projectRepository;
    private final DirectionRepository directionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ProjectDto create(final ProjectCreateDto projectCreateDto) {
        Direction direction = directionRepository.findById(projectCreateDto.getDirectionId()).get();
        Employee employee = employeeRepository.findById(projectCreateDto.getProjectLeadId()).get();
        Project p = new Project()
                .setName(projectCreateDto.getName())
                .setDescription(projectCreateDto.getDescription())
                .setDirection(direction)
                .setProjectLead(employee)
                .setCreateDate(Instant.ofEpochSecond(projectCreateDto.getCreateDate()));;
        projectRepository.save(p);
        return new ProjectDto(p);
    }

    @Override
    public ProjectGetByIdDto get(long id) {
        return new ProjectGetByIdDto(findByIdOrThrow(id));
    }

    private Project findByIdOrThrow(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJ_DESC, id));
    }

    @Override
    public void update(long id, ProjectUpdateDto projectUpdateDto) {
        Direction direction = directionRepository.findById(projectUpdateDto.getDirectionId()).get();
        Employee employee = employeeRepository.findById(projectUpdateDto.getProjectLeadId()).get();

        Project p = findByIdOrThrow(id)
                .setName(projectUpdateDto.getName())
                .setDescription(projectUpdateDto.getDescription())
                .setDirection(direction)
                .setProjectLead(employee);
    }

    @Override
    public void delete(long id) {
        findByIdOrThrow(id);
    }

    @Override
    public PagedData<ProjectDto> search(SearchCriteria criteria) {
        return projectRepository.search(criteria).map(ProjectDto::new);
    }
}
