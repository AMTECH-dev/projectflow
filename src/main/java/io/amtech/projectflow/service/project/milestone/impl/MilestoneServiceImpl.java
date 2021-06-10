package io.amtech.projectflow.service.project.milestone.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.repository.MilestoneRepository;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.project.milestone.MilestoneCreateDto;
import io.amtech.projectflow.service.project.milestone.MilestoneDto;
import io.amtech.projectflow.service.project.milestone.MilestoneService;
import io.amtech.projectflow.service.project.milestone.MilestoneUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MilestoneServiceImpl implements MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Override
    public MilestoneDto create(final long projectId, final MilestoneCreateDto createDto) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> new ObjectNotFoundException("Project", projectId));

        Milestone m = new Milestone()
                .setName(createDto.getName())
                .setDescription(createDto.getDescription())
                .setPlannedStartDate(createDto.getPlannedStartDate())
                .setPlannedFinishDate(createDto.getPlannedFinishDate())
                .setFactStartDate(createDto.getFactStartDate())
                .setFactFinishDate(createDto.getFactFinishDate());

        p.getMilestones().add(m);
        milestoneRepository.save(m);

        return new MilestoneDto(m);
    }

    @Override
    public MilestoneDto get(long id) {
        return new MilestoneDto(findByIdOrThrow(id));
    }

    private Milestone findByIdOrThrow(long id) {
        return milestoneRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Milestone", id));
    }

    @Override
    public void update(long id, MilestoneUpdateDto newData) {
        Milestone m = findByIdOrThrow(id);

        m.setName(newData.getName());
        m.setDescription(newData.getDescription());
        m.setPlannedStartDate(newData.getPlannedStartDate());
        m.setPlannedFinishDate(newData.getPlannedFinishDate());
        m.setFactStartDate(newData.getFactStartDate());
        m.setFactFinishDate(newData.getFactFinishDate());
    }

    @Override
    public void delete(long id) {
        findByIdOrThrow(id);
        milestoneRepository.deleteById(id);
    }

    @Override
    public PagedData<MilestoneDto> search(SearchCriteria criteria) {
        return null;
    }
}