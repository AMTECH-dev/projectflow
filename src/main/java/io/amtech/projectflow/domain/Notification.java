package io.amtech.projectflow.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;

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
    String recepient;   //    recepient varchar(50) not null check (recepient ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$),

    @Column(name = "sender", nullable = false)
    String sender;  //    sender varchar(50) not null check (sender ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$),

    @Column(name = "subject")
    String subject;

    @Column(name = "body")
    String body;

    @Column(name = "create_date", nullable = false)
    Date createDate;
}
