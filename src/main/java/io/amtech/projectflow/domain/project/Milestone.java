package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "milestone", schema = "pf")
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne() // fetch = FetchType.EAGER
    @JoinColumn(name = "project_id")
    Project project;

    @Column(name="name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "planned_start_date", nullable = false)
    Timestamp plannedStartDate;

    @Column(name = "planned_finish_date", nullable = false)
    Timestamp plannedFinishDate;

    @Column(name = "fact_start_date")
    Timestamp factStartDate;

    @Column(name = "fact_finish_date")
    Timestamp factFinishDate;

    @Column(name = "progress_percent", nullable = false)
    short progressRercent = 0;
}
