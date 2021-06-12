package io.amtech.projectflow.service.audit.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.repository.ProjectJournalRepository;
import io.amtech.projectflow.service.audit.AuditProject;
import io.amtech.projectflow.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class AuditServiceImpl implements AuditService {
    private final ObjectMapper objectMapper;
    private final ProjectJournalRepository projectJournalRepository;

    @Override
    public void save(String username, Project project) {
        Map<String, Object> currentProjectState = objectMapper.convertValue(new AuditProject(project), new TypeReference<>() {});
        ProjectJournal projectJournal = new ProjectJournal()
                .setLogin(username)
                .setUpdateDate(Instant.now())
                .setCurrentState(currentProjectState);
        project.getHistory().add(projectJournal);
        projectJournalRepository.save(projectJournal);
    }
}
