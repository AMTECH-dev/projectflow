package io.amtech.projectflow.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "notification", schema = "pf")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "recepient", nullable = false)
    private String recepient;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "create_date", nullable = false)
    private Instant createDate;
}
