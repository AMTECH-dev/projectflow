package io.amtech.projectflow.service.employee.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.service.employee.EmployeeCreateDto;
import io.amtech.projectflow.service.employee.EmployeeDto;
import io.amtech.projectflow.service.employee.EmployeeService;
import io.amtech.projectflow.service.employee.EmployeeUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto create(final EmployeeCreateDto createDto) {
        Employee e = new Employee()
                .setName(createDto.getName())
                .setEmail(createDto.getEmail())
                .setPhone(createDto.getPhone())
                .setPosition(createDto.getPosition());
        Employee savedEmployee = employeeRepository.save(e);

        return new EmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto get(final long id) {
        return new EmployeeDto(employeeRepository.findByIdOrThrow(id));
    }

    @Override
    public void update(final long id, final EmployeeUpdateDto newData) {
        Employee e = employeeRepository.findByIdOrThrow(id);
        e.setName(newData.getName());
        e.setEmail(newData.getEmail());
        e.setPhone(newData.getPhone());
        e.setPosition(newData.getPosition());
        e.setFired(newData.getIsFired());
    }

    @Override
    public void delete(final long id) {
        employeeRepository.findByIdOrThrow(id);
        employeeRepository.deleteById(id);
    }

    @Override
    public PagedData<EmployeeDto> search(final SearchCriteria criteria) {
        return employeeRepository.search(criteria).map(EmployeeDto::new);
    }
}
