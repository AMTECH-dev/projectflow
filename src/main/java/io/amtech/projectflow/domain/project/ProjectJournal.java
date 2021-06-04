package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "project_journal", schema = "pf")
public class ProjectJournal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "update_date", nullable = false)
    private Instant updateDate;

    @Column(name = "current_state", nullable = false)
    private String currentState;
}
