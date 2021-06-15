package io.amtech.projectflow.service.project.milestone.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
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

import static io.amtech.projectflow.util.DateUtil.millisToInstant;

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
                .setPlannedStartDate(millisToInstant(createDto.getPlannedStartDate()))
                .setPlannedFinishDate(millisToInstant(createDto.getPlannedFinishDate()))
                .setFactStartDate(millisToInstant(createDto.getFactStartDate()))
                .setFactFinishDate(millisToInstant(createDto.getFactFinishDate()))
                .setProgressPercent(createDto.getProgressPercent());

        p.getMilestones().add(m);
        milestoneRepository.save(m);

        return new MilestoneDto(m);
    }

    @Override
    public MilestoneDto get(final long projectId, final long milestoneId) {
        findProjectByIdOrThrow(projectId);
        return new MilestoneDto(findMilestoneByIdOrThrow(milestoneId));
    }

    @Override
    public void update(final long projectId, final long milestoneId, final MilestoneUpdateDto newData) {
        findProjectByIdOrThrow(projectId);

        Milestone m = findMilestoneByIdOrThrow(milestoneId);
        m.setName(newData.getName());
        m.setDescription(newData.getDescription());
        m.setPlannedStartDate(millisToInstant(newData.getPlannedStartDate()));
        m.setPlannedFinishDate(millisToInstant(newData.getPlannedFinishDate()));
        m.setFactStartDate(millisToInstant(newData.getFactStartDate()));
        m.setFactFinishDate(millisToInstant(newData.getFactFinishDate()));
        m.setProgressPercent(newData.getProgressPercent());
    }

    @Override
    public void updateProgressPercent(final long projectId,
                                      final long milestoneId,
                                      final MilestoneUpdateProgressDto updateProgressDto) {
        findProjectByIdOrThrow(projectId);

        Milestone m = findMilestoneByIdOrThrow(milestoneId);
        m.setProgressPercent(updateProgressDto.getProgressPercent());
    }

    @Override
    public void delete(final long projectId, final long milestoneId) {
        findProjectByIdOrThrow(projectId);
        findMilestoneByIdOrThrow(milestoneId);
        milestoneRepository.deleteById(milestoneId);
    }

    @Override
    public PagedData<MilestoneDto> search(final long projectId, final SearchCriteria criteria) {
        return milestoneRepository.search(projectId, criteria).map(MilestoneDto::new);
    }

    private Milestone findMilestoneByIdOrThrow(final long id) {
        return milestoneRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Milestone.class.getSimpleName(), id));
    }

    private void findProjectByIdOrThrow(final long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ObjectNotFoundException(Project.class.getSimpleName(), projectId));
    }
}
