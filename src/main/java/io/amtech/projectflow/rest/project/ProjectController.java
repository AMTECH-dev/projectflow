package io.amtech.projectflow.rest.project;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.service.project.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.amtech.projectflow.util.ConvertingUtil.objToString;
import static io.amtech.projectflow.util.SearchUtil.FROM_DATE_KEY;
import static io.amtech.projectflow.util.SearchUtil.TO_DATE_KEY;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@RequestBody @Valid ProjectCreateDto dto) {
        return projectService.create(dto);
    }

    @PutMapping({"{id}"})
    public void update(@PathVariable long id, @RequestBody @Valid ProjectUpdateDto dto) {
        projectService.update(id, dto);
    }

    @GetMapping({"{id}"})
    public ProjectGetByIdDto get(@PathVariable long id) {
        return projectService.get(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable() long id) {
        projectService.delete(id);
    }


    @GetMapping
    public PagedData<ProjectDto> search(@RequestParam(required = false, defaultValue = "100") Integer limit,
                                        @RequestParam(required = false, defaultValue = "0") Integer offset,
                                        @RequestParam(required = false, defaultValue = "name") String orders,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) Long projectLeadId,
                                        @RequestParam(required = false) Long directionId,
                                        @RequestParam(required = false) Long createDateFrom,
                                        @RequestParam(required = false) Long createDateTo,
                                        @RequestParam(required = false) String status) {
        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter(Project_.NAME, name)
                .filter(Project_.PROJECT_LEAD, objToString(projectLeadId))
                .filter(Project_.DIRECTION, objToString(directionId))
                .filter(Project_.CREATE_DATE + FROM_DATE_KEY, objToString(createDateFrom))
                .filter(Project_.CREATE_DATE + TO_DATE_KEY, objToString(createDateTo))
                .filter(Project_.PROJECT_STATUS, status)
                .order(orders)
                .build();

        return projectService.search(criteria);
    }
}