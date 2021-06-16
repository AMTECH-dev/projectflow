package io.amtech.projectflow.rest.project.milestone;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.project.Milestone_;
import io.amtech.projectflow.service.project.milestone.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.amtech.projectflow.util.ConvertingUtil.objToString;

@RestController
@RequestMapping("/projects/{projectId}/milestones")
@RequiredArgsConstructor
public class MilestoneController {
    private final MilestoneService milestoneService;

    @PostMapping
    public MilestoneDto create(@PathVariable long projectId,
                               @RequestBody @Valid MilestoneCreateDto dto) {
        return milestoneService.create(projectId, dto);
    }

    @GetMapping("{milestoneId}")
    public MilestoneDto get(@PathVariable long projectId,
                            @PathVariable long milestoneId) {
        return milestoneService.get(projectId, milestoneId);
    }

    @PutMapping("{milestoneId}")
    public void update(@PathVariable long projectId,
                       @PathVariable long milestoneId,
                       @RequestBody @Valid MilestoneUpdateDto dto) {
        milestoneService.update(projectId, milestoneId, dto);
    }

    @PatchMapping("{milestoneId}")
    public void updateProgressPercent(@PathVariable long projectId,
                                      @PathVariable long milestoneId,
                                      @RequestBody @Valid MilestoneUpdateProgressDto updateProgressDto) {
        milestoneService.updateProgressPercent(projectId, milestoneId, updateProgressDto);
    }

    @DeleteMapping("{milestoneId}")
    public void delete(@PathVariable long projectId,
                       @PathVariable long milestoneId) {
        milestoneService.delete(projectId, milestoneId);
    }

    @GetMapping
    public PagedData<MilestoneDto> search(@PathVariable long projectId,
                                          @RequestParam(required = false, defaultValue = "100") Integer limit,
                                          @RequestParam(required = false, defaultValue = "0") Integer offset,
                                          @RequestParam(required = false, defaultValue = "name") String orders,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) Long plannedStartDateFrom,
                                          @RequestParam(required = false) Long plannedStartDateTo,
                                          @RequestParam(required = false) Long plannedFinishDateFrom,
                                          @RequestParam(required = false) Long plannedFinishDateTo,
                                          @RequestParam(required = false) Long factStartDateFrom,
                                          @RequestParam(required = false) Long factStartDateTo,
                                          @RequestParam(required = false) Long factFinishDateFrom,
                                          @RequestParam(required = false) Long factFinishDateTo,
                                          @RequestParam(required = false) Short progressPercent) {
        final String fromRangeKey = "From";
        final String toRangeKey = "To";

        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter(Milestone_.NAME, name)
                .filter(Milestone_.PLANNED_START_DATE + fromRangeKey, objToString(plannedStartDateFrom))
                .filter(Milestone_.PLANNED_START_DATE + toRangeKey, objToString(plannedStartDateTo))
                .filter(Milestone_.PLANNED_FINISH_DATE + fromRangeKey, objToString(plannedFinishDateFrom))
                .filter(Milestone_.PLANNED_FINISH_DATE + toRangeKey, objToString(plannedFinishDateTo))
                .filter(Milestone_.FACT_START_DATE + fromRangeKey, objToString(factStartDateFrom))
                .filter(Milestone_.FACT_START_DATE + toRangeKey, objToString(factStartDateTo))
                .filter(Milestone_.FACT_FINISH_DATE + fromRangeKey, objToString(factFinishDateFrom))
                .filter(Milestone_.FACT_FINISH_DATE + toRangeKey, objToString(factFinishDateTo))
                .filter(Milestone_.PROGRESS_PERCENT, objToString(progressPercent))
                .order(orders)
                .build();
        return milestoneService.search(projectId, criteria);
    }
}
