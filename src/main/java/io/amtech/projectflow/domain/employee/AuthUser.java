package io.amtech.projectflow.domain.employee;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "auth_user", schema = "pf")
public class AuthUser {
    @Id
    @Column(name = "employee_id")
    private Long id;

    @Column(name="login", nullable = false)
    private String login;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="is_locked", nullable = false)
    private boolean isLocked;
}
