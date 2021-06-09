package io.amtech.projectflow.domain.project;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(exclude = "project")
@Accessors(chain = true)
@Entity
@Table(name = "project_journal", schema = "pf")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ProjectJournal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "update_date", nullable = false)
    private Instant updateDate;

    @Type(type = "jsonb")
    @Column(name = "current_state", columnDefinition = "jsonb")
    private Map<String, Object> currentState = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
