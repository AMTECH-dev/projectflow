package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "project_comment", schema = "pf")
public class ProjectComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name="create_date", nullable = false)
    private Instant createDate;

    @Column(name="login", nullable = false)
    private String login;
}
