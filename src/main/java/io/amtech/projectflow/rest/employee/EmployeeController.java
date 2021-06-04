package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.service.employee.EmployeeCreateDto;
import io.amtech.projectflow.service.employee.EmployeeUpdateDto;
import io.amtech.projectflow.service.employee.EmployeeDto;
import io.amtech.projectflow.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}
