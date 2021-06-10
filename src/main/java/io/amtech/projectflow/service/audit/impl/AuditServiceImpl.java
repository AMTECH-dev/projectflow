package io.amtech.projectflow.service.audit.impl;

import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.repository.ProjectJournalRepository;
import io.amtech.projectflow.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class AuditServiceImpl implements AuditService {
    private final ProjectJournalRepository projectJournalRepository;

    @Override
    public void save(String username, Project project) {
        Map<String, Object> currentProjectState = new HashMap<>() {{
            put("name", project.getName());
            put("projectLeadId", project.getProjectLead().getId());
            put("directionId", project.getDirection().getId());
            put("description", project.getDescription());
            put("createDate", project.getCreateDate());
            put("milestones", project.getMilestones());
            put("status", project.getProjectStatus());
        }};

        ProjectJournal projectJournal = new ProjectJournal()
                .setLogin(username)
                .setUpdateDate(Instant.now())
                .setCurrentState(currentProjectState);
        project.getHistory().add(projectJournal);
        projectJournalRepository.save(projectJournal);
    }
}
