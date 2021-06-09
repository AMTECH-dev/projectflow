package io.amtech.projectflow.domain.employee;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import io.amtech.projectflow.domain.project.Project;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "employee", schema = "pf")
@TypeDef(
        name = "user_position",
        typeClass = PostgreSQLEnumType.class
)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "position", nullable = false)
    @Type( type = "user_position")
    private UserPosition position;

    @Column(name = "is_fired", nullable = false)
    private boolean isFired;

    @OneToMany(mappedBy = "projectLead")
    private Set<Project> projects = new HashSet<>();
}
