package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;
import java.util.Map;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "project_journal", schema = "pf")
public class ProjectJournal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "update_date", nullable = false)
    private Instant updateDate;

    private Map<String, Object> currentState;
}
