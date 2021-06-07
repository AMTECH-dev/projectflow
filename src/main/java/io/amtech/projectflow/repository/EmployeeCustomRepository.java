package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.employee.Employee;

public interface EmployeeCustomRepository {
    PagedData<Employee> search(SearchCriteria criteria);
}
