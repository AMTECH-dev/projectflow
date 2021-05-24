package io.amtech.projectflow.persistence.employee;

import io.amtech.projectflow.domain.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
