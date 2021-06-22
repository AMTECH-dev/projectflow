package io.amtech.projectflow.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "token", schema = "pf")
public class Token {
    @Id
    @Column(name = "access_token", unique = true, nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
}
