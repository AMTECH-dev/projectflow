package io.amtech.projectflow.service.project;

public interface ProjectService {
    ProjectDto create(ProjectCreateDto createDto);
    ProjectDto get(long id);
    void update(long id, ProjectUpdateDto newData);
//    void delete(long id); // реализовать в данномметоде перевод в состояние "DONE\CLOSED" или не использовать вообще?
}
