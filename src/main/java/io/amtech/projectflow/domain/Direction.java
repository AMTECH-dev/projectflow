package io.amtech.projectflow.domain;

import io.amtech.projectflow.domain.employee.Employee;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "direction", schema = "pf")
public class Direction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="lead_id")
    Employee lead;

    @Column(name = "name", nullable = false)
    String name;
}