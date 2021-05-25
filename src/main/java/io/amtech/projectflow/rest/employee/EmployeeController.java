package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.service.employee.EmployeeCreateDto;
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
    EmployeeDto create(@RequestBody @Valid EmployeeCreateDto dto) {
        return employeeService.create(dto);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        employeeService.delete(id);
    }
}
