package io.amtech.projectflow.domain.employee;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "auth_user", schema = "pf")
public class AuthUser {
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="employee")
    Employee employee;

    @Column(name="login", nullable = false)
    String login;

    @Column(name="password", nullable = false)
    String password;

    @Column(name="is_locked", nullable = false)
    Boolean isLocked = false;

//    constraint auth_user_has_employee_fk foreign key (employee_id) references pf.employee (id) on delete cascade on update cascade
}
