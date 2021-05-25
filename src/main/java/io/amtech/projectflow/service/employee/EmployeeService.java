package io.amtech.projectflow.service.employee;

public interface EmployeeService {
    EmployeeDto create(EmployeeCreateDto createDto);
    EmployeeDto get(long id);
    EmployeeDto update(long id, EmployeeUpdateDto newData);
    void delete(long id);
}
