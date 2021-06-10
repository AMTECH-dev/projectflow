package io.amtech.projectflow.rest.project;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.service.project.milestone.MilestoneCreateDto;
import io.amtech.projectflow.service.project.milestone.MilestoneDto;
import io.amtech.projectflow.service.project.milestone.MilestoneService;
import io.amtech.projectflow.service.project.milestone.MilestoneUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
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
                                          @RequestParam(required = false) Instant plannedStartDate,
                                          @RequestParam(required = false) Instant plannedFinishDate,
                                          @RequestParam(required = false) Instant factStartDate,
                                          @RequestParam(required = false) Instant factFinishDate,
                                          @RequestParam(required = false) Short progressPercent) {
        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter("name", name)
                .filter("plannedStartDate", objToString(plannedStartDate))
                .filter("plannedFinishDate", objToString(plannedFinishDate))
                .filter("factStartDate", objToString(factStartDate))
                .filter("factFinishDate", objToString(factFinishDate))
                .filter("progressPercent", objToString(progressPercent))
                .order(orders)
                .build();
        return milestoneService.search(criteria);
    }

    private static String objToString(Object o) {
        return Optional.ofNullable(o).map(Objects::toString).orElse(null);
    }
}
