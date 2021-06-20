package io.amtech.projectflow.rest.project_journal;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.project.ProjectJournal_;
import io.amtech.projectflow.service.project_journal.ProjectJournalDto;
import io.amtech.projectflow.service.project_journal.ProjectJournalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import static io.amtech.projectflow.util.ConvertingUtil.objToString;

@RestController
@RequestMapping("/projects/{projectId}/projectJournals")
@RequiredArgsConstructor
public class ProjectJournalController {
    private final ProjectJournalService projectJournalService;

    @GetMapping
    @ApiOperation("Поиск записей в журнале")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "400", description = "Некорректный параметер orders")
    public PagedData<ProjectJournalDto> search(@PathVariable long projectId,
                                               @RequestParam(name = "limit", defaultValue = "100") Integer limit,
                                               @RequestParam(name = "offset", defaultValue = "0") Integer offset,
                                               @RequestParam(name = "orders", defaultValue = "login") String orders,
                                               @RequestParam(name = "login", required = false) String login,
                                               @RequestParam(name = "updateDateFrom", required = false) Long updateDateFromInSecond,
                                               @RequestParam(name = "updateDateTo", required = false) Long updateDateToInSecond) {
        SearchCriteria searchCriteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .order(orders)
                .filter(ProjectJournal_.LOGIN, objToString(login))
                .filter(ProjectJournal_.UPDATE_DATE + "From", objToString(updateDateFromInSecond))
                .filter(ProjectJournal_.UPDATE_DATE + "To", objToString(updateDateToInSecond))
                .build();

        return projectJournalService.search(projectId, searchCriteria);
    }

    @GetMapping("{id}")
    @ApiOperation("Получение записи в журнале по id")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Проект не найден")
    @ApiResponse(responseCode = "404", description = "Запись в журнале не найдена")
    public ProjectJournalDto get(@PathVariable long projectId, @PathVariable long id) {
        return projectJournalService.get(projectId, id);
    }
}
