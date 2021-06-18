package io.amtech.projectflow.rest.project_comment;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.project.ProjectComment_;
import io.amtech.projectflow.service.project_comment.ProjectCommentCreateDto;
import io.amtech.projectflow.service.project_comment.ProjectCommentDto;
import io.amtech.projectflow.service.project_comment.ProjectCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.amtech.projectflow.util.ConvertingUtil.objToString;

@RestController
@RequestMapping("/projects/{projectId}/comments")
@RequiredArgsConstructor
public class ProjectCommentController {
    private final ProjectCommentService projectCommentService;

    @PostMapping
    public ProjectCommentDto create(@PathVariable long projectId, @RequestBody @Valid ProjectCommentCreateDto dto) {
        return projectCommentService.create(projectId, dto);
    }

    @PutMapping("{id}")
    public void update(@PathVariable long projectId, @PathVariable long id, @RequestBody @Valid ProjectCommentCreateDto dto) {
        projectCommentService.update(projectId, id, dto);
    }

    @GetMapping("{id}")
    public ProjectCommentDto get(@PathVariable long projectId, @PathVariable long id) {
        return projectCommentService.get(projectId, id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long projectId, @PathVariable long id) {
        projectCommentService.delete(projectId, id);
    }

    @GetMapping
    public PagedData<ProjectCommentDto> search(@PathVariable long projectId,
                                               @RequestParam(required = false, defaultValue = "100") Integer limit,
                                               @RequestParam(required = false, defaultValue = "0") Integer offset,
                                               @RequestParam(required = false, defaultValue = "login") String orders,
                                               @RequestParam(required = false) String login,
                                               @RequestParam(required = false) String message,
                                               @RequestParam(required = false) Long createDateFrom,
                                               @RequestParam(required = false) Long createDateTo) {
        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter(ProjectComment_.LOGIN, login)
                .filter(ProjectComment_.MESSAGE, message)
                .filter(ProjectComment_.CREATE_DATE + "From", objToString(createDateFrom))
                .filter(ProjectComment_.CREATE_DATE + "To", objToString(createDateTo))
                .order(orders)
                .build();

        return projectCommentService.search(projectId, criteria);
    }
}
