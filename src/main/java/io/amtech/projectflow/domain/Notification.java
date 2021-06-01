package io.amtech.projectflow.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "notification", schema = "pf")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    recepient varchar(50) not null check (recepient ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$),
//    sender varchar(50) not null check (sender ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$),

    @Column(name = "subject")
    String subject;

    @Column(name = "body")
    String body;

    @Column(name = "create_date", nullable = false)
    Object createDate;
}
