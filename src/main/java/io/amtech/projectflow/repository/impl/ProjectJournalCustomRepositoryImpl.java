package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.domain.project.ProjectJournal_;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.repository.ProjectJournalCustomRepository;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        List<Predicate> predicates = new ArrayList<>(){{
            add(builder.equal(root.get(Project_.ID), projectId));
        }};
        criteria.getFilter(ProjectJournal_.LOGIN)
                .ifPresent(v -> predicates.add(builder.like(builder.lower(joinProjectJournal.get(ProjectJournal_.LOGIN)),
                        "%" + v.toLowerCase() + "%")));
        criteria.getFilter(ProjectJournal_.UPDATE_DATE + "From")
                .ifPresent(v -> predicates.add(builder.greaterThanOrEqualTo(joinProjectJournal.get(ProjectJournal_.UPDATE_DATE),
                        toInstant(Long.parseLong(v)))));
        criteria.getFilter(ProjectJournal_.UPDATE_DATE + "To")
                .ifPresent(v -> predicates.add(builder.lessThanOrEqualTo(joinProjectJournal.get(ProjectJournal_.UPDATE_DATE),
                        toInstant(Long.parseLong(v)))));
        query.select(joinProjectJournal)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(joinProjectJournal, criteria.getOrder()));

        List<ProjectJournal> result = em.createQuery(query)
                .setMaxResults(criteria.getLimit())
                .setFirstResult(criteria.getOffset())
                .getResultList();

        return new PagedData<>(result, criteria);
    }


    private Instant toInstant(long second) {
        return new Timestamp(fromSecondToMillisecond(second)).toInstant();
    }

    private long fromSecondToMillisecond(long second) {
        return second * 1000L;
    }

    private Order parseOrder(Join<?, ?> root, final String order) {
        try {
            if (order.startsWith("-")) {
                return new OrderImpl(root.get(order.substring(1)), false);
            }

            return new OrderImpl(root.get(order));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(order);
        }
    }
}
