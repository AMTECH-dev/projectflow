package io.amtech.projectflow.service.employee.impl;

import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.persistence.employee.EmployeeRepository;
import io.amtech.projectflow.service.employee.EmployeeCreateDto;
import io.amtech.projectflow.service.employee.EmployeeDto;
import io.amtech.projectflow.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeDto create(final EmployeeCreateDto createDto) {
        Employee e = new Employee()
                .setName(createDto.getName())
                .setEmail(createDto.getEmail())
                .setPhone(createDto.getPhone())
                .setPosition(createDto.getPosition());
        employeeRepository.save(e);

        return new EmployeeDto(e);
    }
}
