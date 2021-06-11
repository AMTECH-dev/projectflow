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

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MilestoneServiceImpl implements MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Override
    public MilestoneDto create(final long projectId, final MilestoneCreateDto createDto) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> new ObjectNotFoundException(Project.class.getSimpleName(), projectId));

        Milestone m = new Milestone()
                .setName(createDto.getName())
                .setDescription(createDto.getDescription())
                .setPlannedStartDate(Instant.ofEpochMilli(createDto.getPlannedStartDate()))
                .setPlannedFinishDate(Instant.ofEpochMilli(createDto.getPlannedFinishDate()))
                .setFactStartDate(timestampToInstant(createDto.getFactStartDate()))
                .setFactFinishDate(timestampToInstant(createDto.getFactFinishDate()))
                .setProgressPercent(createDto.getProgressPercent());

        p.getMilestones().add(m);
        milestoneRepository.save(m);

        return new MilestoneDto(m);
    }

    private static Instant timestampToInstant(Long l) {
        return Optional.ofNullable(l)
                .map(Instant::ofEpochMilli)
                .orElse(null);
    }

    @Override
    public MilestoneDto get(long id) {
        return new MilestoneDto(findByIdOrThrow(id));
    }

    private Milestone findByIdOrThrow(long id) {
        return milestoneRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Milestone.class.getSimpleName(), id));
    }

    @Override
    public void update(long id, MilestoneUpdateDto newData) {
        Milestone m = findByIdOrThrow(id);

        m.setName(newData.getName());
        m.setDescription(newData.getDescription());
        m.setPlannedStartDate(Instant.ofEpochMilli(newData.getPlannedStartDate()));
        m.setPlannedFinishDate(Instant.ofEpochMilli(newData.getPlannedFinishDate()));
        m.setFactStartDate(Instant.ofEpochMilli(newData.getFactStartDate()));
        m.setFactFinishDate(Instant.ofEpochMilli(newData.getFactFinishDate()));
        m.setProgressPercent(newData.getProgressPercent());
    }

    @Override
    public void delete(long id) {
        findByIdOrThrow(id);
        milestoneRepository.deleteById(id);
    }

    @Override
    public PagedData<MilestoneDto> search(SearchCriteria criteria) {
        return milestoneRepository.search(criteria).map(MilestoneDto::new);
    }
}