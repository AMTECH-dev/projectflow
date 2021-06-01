package io.amtech.projectflow.domain.project;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

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
    String name;

    @Column(name = "project_lead_id")
    long projectLeadId;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "project_lead")
//    Employee projectLead;

    @Column(name = "direction_id")
    long directionId;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "direction", referencedColumnName = "id")
//    Direction direction;

    @Column(name = "description")
    String description;

    @Column(name = "create_date", nullable = false)
    Object createDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "project", nullable = false)
    @Type( type = "project_status")
    private ProjectStatus projectStatus;

//    constraint project_has_project_lead_fk foreign key(project_lead_id) references pf.employee(id) on delete restrict on update cascade,
//    constraint project_has_direction_fk foreign key(direction_id) references pf.direction(id) on delete restrict on update cascade
}