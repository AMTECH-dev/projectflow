package io.amtech.projectflow.domain.project;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "milestone", schema = "pf")
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @Column(name = "project", nullable = false)
//    Project project;

    @Column(name="name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "planned_start_date", nullable = false)
    Object plannedStartDate;

    @Column(name = "planned_finish_date", nullable = false)
    Object plannedFinishDate;

    @Column(name = "fact_start_date")
    Object factStartDate;

    @Column(name = "fact_finish_date")
    Object factFinishDate;

    @Column(name = "progress_percent", nullable = false)
    short progressRercent = 0;

//    constraint milestone_has_project_fk foreign key(project_id) references pf.project(id) on delete cascade on update cascade
}
