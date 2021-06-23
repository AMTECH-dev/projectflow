package io.amtech.projectflow.domain;

import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = "lead")
@ToString(exclude = "lead")
@Accessors(chain = true)
@Entity
@Table(name = "direction", schema = "pf")
public class Direction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lead_id")
    private Employee lead;

    @Column(name = "name", nullable = false)
    private String name;
}
