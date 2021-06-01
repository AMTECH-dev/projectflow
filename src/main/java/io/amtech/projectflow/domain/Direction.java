package io.amtech.projectflow.domain;

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

    @Column(name = "lead_id", nullable = false)
    long lead_id;

    @Column(name = "name", nullable = false)
    String name;

//    constraint direction_has_lead_fk foreign key(lead_id) references pf.employee(id) on delete restrict on update cascade
}
