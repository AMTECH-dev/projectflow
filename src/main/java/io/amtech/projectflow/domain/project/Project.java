package io.amtech.projectflow.domain.project;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Date;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_lead")
    Employee projectLead;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direction", referencedColumnName = "id")
    Direction direction;

    @Column(name = "description")
    String description;

    @Column(name = "create_date", nullable = false)
    Date createDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "status", nullable = false)
    @Type( type = "project_status")
    private ProjectStatus projectStatus;
}