package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "project_comment", schema = "pf")
public class ProjectComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @Column(name = "project", nullable = false)
//    Project project;

    @Column(name = "message", nullable = false)
    String message;

    @Column(name="create_date", nullable = false)
    Object createDate; // default now()

    @Column(name="login", nullable = false)
    String login;

//    constraint project_comment_has_project_fk foreign key(project_id) references pf.project(id) on delete cascade on update cascade
}
