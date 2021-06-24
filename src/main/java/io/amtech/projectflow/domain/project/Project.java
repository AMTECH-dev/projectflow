package io.amtech.projectflow.domain.project;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_lead_id")
    private Employee projectLead;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direction_id")
    private Direction direction;

    @Column(name = "description")
    private String description;

    @EqualsAndHashCode.Exclude
    @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "status", nullable = false)
    @Type(type = "project_status")
    private ProjectStatus projectStatus = ProjectStatus.UNAPPROVED;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Set<Milestone> milestones = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Set<ProjectComment> projectComments = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Set<ProjectJournal> history = new HashSet<>();
}
