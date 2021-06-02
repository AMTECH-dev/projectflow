package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;

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
    Date plannedStartDate;

    @Column(name = "planned_finish_date", nullable = false)
    Date plannedFinishDate;

    @Column(name = "fact_start_date")
    Date factStartDate;

    @Column(name = "fact_finish_date")
    Date factFinishDate;

    @Column(name = "progress_percent", nullable = false)
    short progressRercent = 0;
}