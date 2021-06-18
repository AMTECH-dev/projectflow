package io.amtech.projectflow.rest;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.repository.direction.DirectionDto;
import io.amtech.projectflow.repository.direction.DirectionCreateDto;
import io.amtech.projectflow.repository.direction.DirectionService;
import io.amtech.projectflow.repository.direction.DirectionUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/directions")
@RequiredArgsConstructor
public class DirectionController {

    private final DirectionService directionService;

    @PostMapping
    DirectionDto create(@RequestBody @Valid DirectionCreateDto dto) {
        return directionService.create(dto);
    }

    @GetMapping("{id}")
    public DirectionDto get(@PathVariable long id) {
        return directionService.get(id);
    }

    @PutMapping("{id}")
    public void update(@PathVariable("{id}") long id, DirectionUpdateDto dto) {
        directionService.update(id, dto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("{id}") long id) {
        directionService.delete(id);
    }

    @GetMapping
    public PagedData<DirectionDto> search(@RequestParam(required = false, defaultValue = "100") Integer limit,
                                          @RequestParam(required = false, defaultValue = "0") Integer offset,
                                          @RequestParam(required = false, defaultValue = "name") String orders,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) Long leadId,
                                          @RequestParam(required = false) String leadName) {
        SearchCriteria criteria=new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter("name",name)
                .filter("leadId", Optional.ofNullable(leadId).map(Objects::toString).orElse(null))
                .filter("leadName",leadName)
                .order(orders)
                .build();
        return directionService.search(criteria);
    }
}
