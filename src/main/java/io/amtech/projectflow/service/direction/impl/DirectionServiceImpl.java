package io.amtech.projectflow.service.direction.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.service.direction.DirectionCreateDto;
import io.amtech.projectflow.service.direction.DirectionDto;
import io.amtech.projectflow.service.direction.DirectionService;
import io.amtech.projectflow.service.direction.DirectionUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectionServiceImpl implements DirectionService {

    private final DirectionRepository directionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public DirectionDto create(DirectionCreateDto createDto) {
        Direction d = new Direction()
                .setName(createDto.getName())
                .setLead(employeeRepository.findByIdOrThrow(createDto.getLeadId()));

        Direction saved=directionRepository.save(d);
        return new DirectionDto(saved);
    }

    @Override
    public DirectionDto get(long id) {
        return new DirectionDto(directionRepository.findByIdOrThrow(id));
    }

    @Override
    public void update(long id, DirectionUpdateDto newData) {
        Direction d=directionRepository.findByIdOrThrow(id);

        d.setName(newData.getName());
        d.setLead(employeeRepository.findByIdOrThrow(newData.getLeadId()));
    }

    @Override
    public void delete(long id) {
        directionRepository.findByIdOrThrow(id);
        directionRepository.deleteById(id);
    }

    @Override
    public PagedData<DirectionDto> search(SearchCriteria criteria) {
        return directionRepository.search(criteria).map(DirectionDto::new);
    }
}
