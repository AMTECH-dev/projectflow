package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "milestone", schema = "pf")
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "planned_start_date", nullable = false)
    private Instant plannedStartDate;

    @Column(name = "planned_finish_date", nullable = false)
    private Instant plannedFinishDate;

    @Column(name = "fact_start_date")
    private Instant factStartDate;

    @Column(name = "fact_finish_date")
    private Instant factFinishDate;

    @Column(name = "progress_percent", nullable = false)
    private short progressPercent = 0;
}
