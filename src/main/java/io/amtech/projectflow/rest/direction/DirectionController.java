package io.amtech.projectflow.rest.direction;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.Direction_;
import io.amtech.projectflow.domain.project.Milestone_;
import io.amtech.projectflow.service.direction.DirectionDto;
import io.amtech.projectflow.service.direction.DirectionCreateDto;
import io.amtech.projectflow.service.direction.DirectionService;
import io.amtech.projectflow.service.direction.DirectionUpdateDto;
import io.amtech.projectflow.util.ConvertingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

import static io.amtech.projectflow.util.ConvertingUtil.objToString;
import static io.amtech.projectflow.util.SearchUtil.FROM_DATE_KEY;
import static io.amtech.projectflow.util.SearchUtil.TO_DATE_KEY;

@RestController
@RequestMapping("/directions")
@RequiredArgsConstructor
public class DirectionController {
    private final DirectionService directionService;

    @PostMapping
    public DirectionDto create(@RequestBody @Valid DirectionCreateDto dto) {
        return directionService.create(dto);
    }

    @GetMapping("{id}")
    public DirectionDto get(@PathVariable long id) {
        return directionService.get(id);
    }

    @PutMapping("{id}")
    public void update(@PathVariable("id") long id, @RequestBody @Valid DirectionUpdateDto dto) {
        directionService.update(id, dto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") long id) {
        directionService.delete(id);
    }

    @GetMapping
    public PagedData<DirectionDto> search(@RequestParam(required = false, defaultValue = "100") Integer limit,
                                          @RequestParam(required = false, defaultValue = "0") Integer offset,
                                          @RequestParam(required = false, defaultValue = "name") String orders,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) Long leadId,
                                          @RequestParam(required = false) String leadName) {
        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter(Direction_.NAME,name)
                .filter("leadId", ConvertingUtil.objToString(leadId))
                .filter("leadName",leadName)
                .order(orders)
                .build();
        return directionService.search(criteria);
    }
}
