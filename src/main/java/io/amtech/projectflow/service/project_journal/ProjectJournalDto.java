package io.amtech.projectflow.service.project_journal;

import io.amtech.projectflow.domain.project.ProjectJournal;
import lombok.Value;

@Value
public class ProjectJournalDto {
    long id;
    String login;
    long updateDate;

    public ProjectJournalDto(final ProjectJournal projectJournal) {
        this.id = projectJournal.getId();
        this.login = projectJournal.getLogin();
        this.updateDate = projectJournal.getUpdateDate().toEpochMilli();
    }
}
