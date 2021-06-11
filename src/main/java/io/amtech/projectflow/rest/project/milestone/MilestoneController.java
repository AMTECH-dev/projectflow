package io.amtech.projectflow.rest.project.milestone;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.project.Milestone_;
import io.amtech.projectflow.service.project.milestone.MilestoneCreateDto;
import io.amtech.projectflow.service.project.milestone.MilestoneDto;
import io.amtech.projectflow.service.project.milestone.MilestoneService;
import io.amtech.projectflow.service.project.milestone.MilestoneUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

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

    @GetMapping("{id}")
    public MilestoneDto get(@PathVariable long id) {
        return milestoneService.get(id);
    }

    @PutMapping("{id}")
    void update(@PathVariable("id") long id,
                @RequestBody @Valid MilestoneUpdateDto dto) {
        milestoneService.update(id, dto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        milestoneService.delete(id);
    }

    @GetMapping
    public PagedData<MilestoneDto> search(@RequestParam(required = false, defaultValue = "100") Integer limit,
                                          @RequestParam(required = false, defaultValue = "0") Integer offset,
                                          @RequestParam(required = false, defaultValue = "name") String orders,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) Long plannedStartDateFrom,
                                          @RequestParam(required = false) Long plannedStartDateTo,
                                          @RequestParam(required = false) Long plannedFinishDate,
                                          @RequestParam(required = false) Long factStartDate,
                                          @RequestParam(required = false) Long factFinishDate,
                                          @RequestParam(required = false) Short progressPercent) {
        final String fromRangeKey = "From";
        final String toRangeKey = "To";

        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter(Milestone_.NAME, name)
                .filter(Milestone_.PLANNED_START_DATE + fromRangeKey, objToString(plannedStartDateFrom))
                .filter(Milestone_.PLANNED_START_DATE + toRangeKey, objToString(plannedStartDateTo))
                .filter(Milestone_.PLANNED_FINISH_DATE + fromRangeKey, objToString(plannedFinishDate))
                .filter(Milestone_.PLANNED_FINISH_DATE + toRangeKey, objToString(plannedFinishDate))
                .filter(Milestone_.FACT_START_DATE + fromRangeKey, objToString(factStartDate))
                .filter(Milestone_.FACT_START_DATE + toRangeKey, objToString(factStartDate))
                .filter(Milestone_.FACT_FINISH_DATE + fromRangeKey, objToString(factFinishDate))
                .filter(Milestone_.FACT_FINISH_DATE + toRangeKey, objToString(factFinishDate))
                .filter(Milestone_.PROGRESS_PERCENT, objToString(progressPercent))
                .order(orders)
                .build();
        return milestoneService.search(criteria);
    }

    private static String objToString(Object o) {
        return Optional.ofNullable(o).map(Objects::toString).orElse(null);
    }
}
