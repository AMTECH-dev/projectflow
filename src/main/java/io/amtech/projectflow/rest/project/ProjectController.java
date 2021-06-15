package io.amtech.projectflow.rest.project;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.service.project.ProjectCreateDto;
import io.amtech.projectflow.service.project.ProjectDto;
import io.amtech.projectflow.service.project.ProjectService;
import io.amtech.projectflow.service.project.ProjectUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
//    private final DirectionRepository directionRepository;

    @PostMapping
    public ProjectDto create(@RequestBody @Valid ProjectCreateDto dto) {
        return projectService.create(dto);
    }

    @PutMapping({"{id}"})
    public void update(@PathVariable long id, @RequestBody @Valid ProjectUpdateDto dto) {
        projectService.update(id, dto);
    }

    @GetMapping({"{id}"})
    public void get(@PathVariable long id) {
        projectService.get(id);
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
                                        @RequestParam(required = false) Long projectLead,
                                        @RequestParam(required = false) Long directionId,
                                        @RequestParam(required = false) Long createDataStart,
                                        @RequestParam(required = false) Long createDataEnd,
                                        @RequestParam(required = false) String status)
                                        {
        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter("name", name)
                .filter("projectLead", Optional.ofNullable(projectLead)
                        .map(Object::toString)
                        .orElse(null))
                .filter("directionId", Optional.ofNullable(directionId)
                        .map(Object::toString)
                        .orElse(null))
                .filter("createDataStart", Optional.ofNullable(createDataStart)
                        .map(Object::toString)
                        .orElse(null))
                .filter("createDataEnd", Optional.ofNullable(createDataEnd)
                        .map(Object::toString)
                        .orElse(null))
                .filter("status", status)
                .order(orders)
                .build();

        return projectService.search(criteria);
    }
}