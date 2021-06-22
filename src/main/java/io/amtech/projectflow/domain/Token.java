package io.amtech.projectflow.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "token", schema = "pf")
public class Token {
    @Id
    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
}
