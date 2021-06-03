package io.amtech.projectflow.service.project.impl;

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

    @Override
    public ProjectDto create(ProjectCreateDto createDto) {
        return null;
    }

    @Override
    public ProjectDto get(long id) {
        return null;
    }

    @Override
    public void update(long id, ProjectUpdateDto newData) {

    }
}
