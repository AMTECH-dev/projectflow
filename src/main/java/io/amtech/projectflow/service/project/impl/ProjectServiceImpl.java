package io.amtech.projectflow.service.project.impl;

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
    private final ProjectRepository projectRepository;
    private final DirectionRepository directionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ProjectDto create(final ProjectCreateDto projectCreateDto) {
        Direction direction = directionRepository.findByIdOrThrow(projectCreateDto.getDirectionId());
        Employee employee = employeeRepository.findByIdOrThrow(projectCreateDto.getProjectLeadId());
        Project p = new Project()
                .setName(projectCreateDto.getName())
                .setDescription(projectCreateDto.getDescription())
                .setDirection(direction)
                .setProjectLead(employee)
                .setCreateDate(Instant.now());

        Project savedProject = projectRepository.save(p);
        return new ProjectDto(savedProject);
    }

    @Override
    public ProjectGetByIdDto get(long id) {
        return new ProjectGetByIdDto(projectRepository.findByIdOrThrow(id));
    }

    @Override
    public void update(long id, ProjectUpdateDto projectUpdateDto) {
        Direction direction = directionRepository.findByIdOrThrow(projectUpdateDto.getDirectionId());
        Employee employee = employeeRepository.findByIdOrThrow(projectUpdateDto.getProjectLeadId());

        Project p = projectRepository.findByIdOrThrow(id);
        p.setName(projectUpdateDto.getName());
        p.setDescription(projectUpdateDto.getDescription());
        p.setDirection(direction);
        p.setProjectLead(employee);
    }

    @Override
    public void delete(long id) {
        projectRepository.findByIdOrThrow(id);
        projectRepository.deleteById(id);
    }

    @Override
    public PagedData<ProjectDto> search(SearchCriteria criteria) {
        return projectRepository.search(criteria).map(ProjectDto::new);
    }
}
