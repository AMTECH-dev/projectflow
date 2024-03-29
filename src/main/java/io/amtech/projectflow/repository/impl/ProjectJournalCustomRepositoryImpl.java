package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.domain.project.ProjectJournal_;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.repository.ProjectJournalCustomRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.amtech.projectflow.util.SearchUtil.*;

@Repository
public class ProjectJournalCustomRepositoryImpl implements ProjectJournalCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public PagedData<ProjectJournal> search(long projectId, SearchCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ProjectJournal> query = builder.createQuery(ProjectJournal.class);
        Root<Project> root = query.from(Project.class);
        Join<Project, ProjectJournal> joinProjectJournal = root.join(Project_.HISTORY);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(Project_.ID), projectId));
        criteria.getFilter(ProjectJournal_.LOGIN)
                .ifPresent(v -> predicates.add(builder.like(builder.lower(joinProjectJournal.get(ProjectJournal_.LOGIN)),
                        "%" + v.toLowerCase() + "%")));
        criteria.getFilter(ProjectJournal_.UPDATE_DATE + FROM_DATE_KEY)
                .ifPresent(v -> predicates.add(builder.greaterThanOrEqualTo(joinProjectJournal.get(ProjectJournal_.UPDATE_DATE),
                        Instant.ofEpochSecond(Long.parseLong(v)))));
        criteria.getFilter(ProjectJournal_.UPDATE_DATE + TO_DATE_KEY)
                .ifPresent(v -> predicates.add(builder.lessThanOrEqualTo(joinProjectJournal.get(ProjectJournal_.UPDATE_DATE),
                        Instant.ofEpochSecond(Long.parseLong(v)))));
        query.select(joinProjectJournal)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(joinProjectJournal, criteria.getOrder()));

        List<ProjectJournal> result = em.createQuery(query)
                .setMaxResults(criteria.getLimit())
                .setFirstResult(criteria.getOffset())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
