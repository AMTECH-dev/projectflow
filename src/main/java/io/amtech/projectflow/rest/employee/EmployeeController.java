package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.employee.UserPosition;
import io.amtech.projectflow.service.employee.EmployeeCreateDto;
import io.amtech.projectflow.service.employee.EmployeeUpdateDto;
import io.amtech.projectflow.service.employee.EmployeeDto;
import io.amtech.projectflow.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public EmployeeDto create(@RequestBody @Valid EmployeeCreateDto dto) {
        return employeeService.create(dto);
    }

    @PutMapping("{id}")
    public void update(@PathVariable long id, @RequestBody @Valid EmployeeUpdateDto dto) {
        employeeService.update(id, dto);
    }

    @GetMapping("{id}")
    public EmployeeDto get(@PathVariable long id) {
        return employeeService.get(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        employeeService.delete(id);
    }

    @GetMapping
    public PagedData<EmployeeDto> search(@RequestParam(required = false, defaultValue = "100") Integer limit,
                                         @RequestParam(required = false, defaultValue = "0") Integer offset,
                                         @RequestParam(required = false, defaultValue = "name") String orders,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String email,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam(required = false) String position,
                                         @RequestParam(required = false) Boolean fired) {
        SearchCriteria criteria = new SearchCriteriaBuilder()
                .limit(limit)
                .offset(offset)
                .filter("name", name)
                .filter("email", email)
                .filter("phone", phone)
                .filter("position", position)
                .filter("fired", Optional.ofNullable(fired)
                        .map(Object::toString)
                        .orElse(null))
                .order(orders)
                .build();

        return employeeService.search(criteria);
    }
}
