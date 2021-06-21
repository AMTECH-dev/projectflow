package io.amtech.projectflow.service.project.milestone.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.repository.MilestoneRepository;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.project.milestone.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.amtech.projectflow.util.ConvertingUtil.secondToInstant;

@Service
@RequiredArgsConstructor
@Transactional
public class MilestoneServiceImpl implements MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Override
    public MilestoneDto create(final long projectId, final MilestoneCreateDto createDto) {
        Project p = projectRepository.findByIdOrThrow(projectId);

        Milestone m = new Milestone()
                .setName(createDto.getName())
                .setDescription(createDto.getDescription())
                .setPlannedStartDate(secondToInstant(createDto.getPlannedStartDate()))
                .setPlannedFinishDate(secondToInstant(createDto.getPlannedFinishDate()))
                .setFactStartDate(secondToInstant(createDto.getFactStartDate()))
                .setFactFinishDate(secondToInstant(createDto.getFactFinishDate()))
                .setProgressPercent(createDto.getProgressPercent());

        p.getMilestones().add(m);
        Milestone savedMilestone = milestoneRepository.save(m);

        return new MilestoneDto(savedMilestone);
    }

    @Override
    public MilestoneDto get(final long projectId, final long milestoneId) {
        projectRepository.findByIdOrThrow(projectId);
        return new MilestoneDto(milestoneRepository.findByIdOrThrow(milestoneId));
    }

    @Override
    public void update(final long projectId, final long milestoneId, final MilestoneUpdateDto newData) {
        projectRepository.findByIdOrThrow(projectId);

        Milestone m = milestoneRepository.findByIdOrThrow(milestoneId);
        m.setName(newData.getName());
        m.setDescription(newData.getDescription());
        m.setPlannedStartDate(secondToInstant(newData.getPlannedStartDate()));
        m.setPlannedFinishDate(secondToInstant(newData.getPlannedFinishDate()));
        m.setFactStartDate(secondToInstant(newData.getFactStartDate()));
        m.setFactFinishDate(secondToInstant(newData.getFactFinishDate()));
        m.setProgressPercent(newData.getProgressPercent());
    }

    @Override
    public void updateProgressPercent(final long projectId,
                                      final long milestoneId,
                                      final MilestoneUpdateProgressDto updateProgressDto) {
        projectRepository.findByIdOrThrow(projectId);

        Milestone m = milestoneRepository.findByIdOrThrow(milestoneId);
        m.setProgressPercent(updateProgressDto.getProgressPercent());
    }

    @Override
    public void delete(final long projectId, final long milestoneId) {
        projectRepository.findByIdOrThrow(projectId);
        milestoneRepository.findByIdOrThrow(milestoneId);
        milestoneRepository.deleteById(milestoneId);
    }

    @Override
    public PagedData<MilestoneDto> search(final long projectId, final SearchCriteria criteria) {
        projectRepository.findByIdOrThrow(projectId);
        return milestoneRepository.search(projectId, criteria).map(MilestoneDto::new);
    }
}
