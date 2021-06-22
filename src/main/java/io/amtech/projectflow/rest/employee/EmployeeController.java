package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.app.general.SearchCriteriaBuilder;
import io.amtech.projectflow.domain.employee.Employee_;
import io.amtech.projectflow.service.employee.EmployeeCreateDto;
import io.amtech.projectflow.service.employee.EmployeeDto;
import io.amtech.projectflow.service.employee.EmployeeService;
import io.amtech.projectflow.service.employee.EmployeeUpdateDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.amtech.projectflow.util.ConvertingUtil.objToString;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @ApiOperation("Создание сотрудника")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "400", description = "Некорректное заполнение полей")
    @ApiResponse(responseCode = "403", description = "Не авторизован или недостаточно прав доступа")
    @PostMapping
    public EmployeeDto create(@RequestBody @Valid EmployeeCreateDto dto) {
        return employeeService.create(dto);
    }

    @ApiOperation("Обновление сотрудника")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    @ApiResponse(responseCode = "400", description = "Некорректное заполнение полей")
    @ApiResponse(responseCode = "403", description = "Не авторизован или недостаточно прав доступа")
    @PutMapping("{id}")
    public void update(@PathVariable long id, @RequestBody @Valid EmployeeUpdateDto dto) {
        employeeService.update(id, dto);
    }

    @ApiOperation("Получение сотрудника по id")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    @ApiResponse(responseCode = "403", description = "Не авторизован или недостаточно прав доступа")
    @GetMapping("{id}")
    public EmployeeDto get(@PathVariable long id) {
        return employeeService.get(id);
    }

    @ApiOperation("Удаление сотрудника")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    @ApiResponse(responseCode = "403", description = "Не авторизован или недостаточно прав доступа")
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        employeeService.delete(id);
    }

    @ApiOperation("Поиск сотрудника по параметрам")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный параметр orders")
    @ApiResponse(responseCode = "403", description = "Не авторизован или недостаточно прав доступа")
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
                .filter(Employee_.NAME, name)
                .filter(Employee_.EMAIL, email)
                .filter(Employee_.PHONE, phone)
                .filter(Employee_.POSITION, position)
                .filter(Employee_.IS_FIRED, objToString(fired))
                .order(orders)
                .build();

        return employeeService.search(criteria);
    }
}
