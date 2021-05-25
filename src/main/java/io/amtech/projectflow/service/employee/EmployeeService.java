package io.amtech.projectflow.service.employee;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;

public interface EmployeeService {
    EmployeeDto create(EmployeeCreateDto createDto);
    EmployeeDto get(long id);
    void update(long id, EmployeeUpdateDto newData);
    void delete(long id);
    PagedData<EmployeeDto> search(SearchCriteria criteria);
}
