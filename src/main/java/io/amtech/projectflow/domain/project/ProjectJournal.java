package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "project_journal", schema = "pf")
public class ProjectJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @Column(name = "project", nullable = false)
//    Project project;

    @Column(name="login", nullable = false)
    String login;

    @Column(name = "update_date", nullable = false)
    Object updateDate;

    @Column(name="current_state", nullable = false)
    Object currentState;

//    constraint project_journal_has_project_fk foreign key(project_id) references pf.project(id) on delete cascade on update cascade
}
