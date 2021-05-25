package io.amtech.projectflow.service.employee.impl;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
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
    private static final String OBJ_DESC = "Employee";
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto create(final EmployeeCreateDto createDto) {
        Employee e = new Employee()
                .setName(createDto.getName())
                .setEmail(createDto.getEmail())
                .setPhone(createDto.getPhone())
                .setPosition(createDto.getPosition());
        employeeRepository.save(e);

        return new EmployeeDto(e);
    }

    @Override
    public EmployeeDto get(long id) {
        return new EmployeeDto(findByIdOrThrow(id));
    }

    private Employee findByIdOrThrow(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJ_DESC, id));
    }

    @Override
    public void update(long id, EmployeeUpdateDto newData) {
        Employee e = findByIdOrThrow(id);
        e.setName(newData.getName());
        e.setEmail(newData.getEmail());
        e.setPhone(newData.getPhone());
        e.setPosition(newData.getPosition());
        e.setFired(newData.getIsFired());
    }

    @Override
    public void delete(long id) {
        findByIdOrThrow(id);

        employeeRepository.deleteById(id);
    }
}
