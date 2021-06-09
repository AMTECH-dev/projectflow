package io.amtech.projectflow.domain.project;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "project", schema = "pf")
@TypeDef(
        name = "project_status",
        typeClass = PostgreSQLEnumType.class
)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_lead_id")
    private Employee projectLead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direction_id")
    private Direction direction;

    @Column(name = "description")
    private String description;

    @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "status", nullable = false)
    @Type( type = "project_status")
    private ProjectStatus projectStatus;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id") // wrong column name
    private List<Milestone> milestones;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private List<ProjectComment> projectComments;
}
