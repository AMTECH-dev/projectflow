package io.amtech.projectflow.rest.project.milestone;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.project.Milestone_;
import io.amtech.projectflow.service.project.milestone.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.amtech.projectflow.util.ConvertingUtil.objToString;
import static io.amtech.projectflow.util.SearchUtil.FROM_DATE_KEY;
import static io.amtech.projectflow.util.SearchUtil.TO_DATE_KEY;

@RestController
@RequestMapping("/projects/{projectId}/milestones")
@RequiredArgsConstructor
public class MilestoneController {
    private final MilestoneService milestoneService;

    @ApiOperation("${milestone.create}")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "400", description = "Некорректное заполнение полей вехи")
    @ApiResponse(responseCode = "403", description = "Не авторизован или недостаточно прав доступа")
    @PostMapping
    public MilestoneDto create(@PathVariable long projectId,
                               @RequestBody @Valid MilestoneCreateDto dto) {
        return milestoneService.create(projectId, dto);
    }

    @ApiOperation("${milestone.get}")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "404", description = "Веха проекта не найдена")
    @GetMapping("{milestoneId}")
    public MilestoneDto get(@PathVariable long projectId,
                            @PathVariable long milestoneId) {
        return milestoneService.get(projectId, milestoneId);
    }

    @ApiOperation("${milestone.update}")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "404", description = "Веха проекта не найдена")
    @ApiResponse(responseCode = "400", description = "Некорректное заполнение полей вехи")
    @PutMapping("{milestoneId}")
    public void update(@PathVariable long projectId,
                       @PathVariable long milestoneId,
                       @RequestBody @Valid MilestoneUpdateDto dto) {
        milestoneService.update(projectId, milestoneId, dto);
    }

    @ApiOperation("${milestone.updateProgressPercent}")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "404", description = "Веха проекта не найдена")
    @ApiResponse(responseCode = "400", description = "Некорректное заполнение процента выполнения вехи проекта")
    @PatchMapping("{milestoneId}")
    public void updateProgressPercent(@PathVariable long projectId,
                                      @PathVariable long milestoneId,
                                      @RequestBody @Valid MilestoneUpdateProgressDto updateProgressDto) {
        milestoneService.updateProgressPercent(projectId, milestoneId, updateProgressDto);
    }

    @ApiOperation("${milestone.delete}")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "404", description = "Веха проекта не найдена")
    @DeleteMapping("{milestoneId}")
    public void delete(@PathVariable long projectId,
                       @PathVariable long milestoneId) {
        milestoneService.delete(projectId, milestoneId);
    }

    @ApiOperation("${milestone.search}")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "400", description = "Некорректный параметр orders")
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

        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter(Milestone_.NAME, name)
                .filter(Milestone_.PLANNED_START_DATE + FROM_DATE_KEY, objToString(plannedStartDateFrom))
                .filter(Milestone_.PLANNED_START_DATE + TO_DATE_KEY, objToString(plannedStartDateTo))
                .filter(Milestone_.PLANNED_FINISH_DATE + FROM_DATE_KEY, objToString(plannedFinishDateFrom))
                .filter(Milestone_.PLANNED_FINISH_DATE + TO_DATE_KEY, objToString(plannedFinishDateTo))
                .filter(Milestone_.FACT_START_DATE + FROM_DATE_KEY, objToString(factStartDateFrom))
                .filter(Milestone_.FACT_START_DATE + TO_DATE_KEY, objToString(factStartDateTo))
                .filter(Milestone_.FACT_FINISH_DATE + FROM_DATE_KEY, objToString(factFinishDateFrom))
                .filter(Milestone_.FACT_FINISH_DATE + TO_DATE_KEY, objToString(factFinishDateTo))
                .filter(Milestone_.PROGRESS_PERCENT, objToString(progressPercent))
                .order(orders)
                .build();
        return milestoneService.search(projectId, criteria);
    }
}
