package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Milestone_;
import io.amtech.projectflow.repository.MilestoneCustomRepository;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MilestoneCustomRepositoryImpl implements MilestoneCustomRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public PagedData<Milestone> search(SearchCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Milestone> query = builder.createQuery(Milestone.class);
        Root<Milestone> root = query.from(Milestone.class);
        List<Predicate> predicates = new ArrayList<>();

        final String fromRangeKey = "From";
        final String toRangeKey = "To";

        criteria.getFilter(Milestone_.NAME)
                .ifPresent(p -> predicates.add(builder.like(root.get(Milestone_.NAME),
                        "%" + p.toLowerCase() + "%")));

        criteria.getFilter(Milestone_.PLANNED_START_DATE + fromRangeKey)
                .ifPresent(p -> predicates.add(builder.greaterThanOrEqualTo(root.get(Milestone_.PLANNED_START_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));
        criteria.getFilter(Milestone_.PLANNED_START_DATE + toRangeKey)
                .ifPresent(p -> predicates.add(builder.lessThanOrEqualTo(root.get(Milestone_.PLANNED_START_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));

        criteria.getFilter(Milestone_.PLANNED_FINISH_DATE + fromRangeKey)
                .ifPresent(p -> predicates.add(builder.greaterThanOrEqualTo(root.get(Milestone_.PLANNED_FINISH_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));
        criteria.getFilter(Milestone_.PLANNED_FINISH_DATE + toRangeKey)
                .ifPresent(p -> predicates.add(builder.lessThanOrEqualTo(root.get(Milestone_.PLANNED_FINISH_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));

        criteria.getFilter(Milestone_.FACT_START_DATE + fromRangeKey)
                .ifPresent(p -> predicates.add(builder.greaterThanOrEqualTo(root.get(Milestone_.FACT_START_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));
        criteria.getFilter(Milestone_.FACT_START_DATE + toRangeKey)
                .ifPresent(p -> predicates.add(builder.lessThanOrEqualTo(root.get(Milestone_.FACT_START_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));

        criteria.getFilter(Milestone_.FACT_FINISH_DATE + fromRangeKey)
                .ifPresent(p -> predicates.add(builder.greaterThanOrEqualTo(root.get(Milestone_.FACT_FINISH_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));
        criteria.getFilter(Milestone_.FACT_FINISH_DATE + toRangeKey)
                .ifPresent(p -> predicates.add(builder.lessThanOrEqualTo(root.get(Milestone_.FACT_FINISH_DATE),
                        Instant.ofEpochMilli(Long.parseLong(p)))));

        criteria.getFilter(Milestone_.PROGRESS_PERCENT)
                .ifPresent(p -> predicates.add(builder.equal(root.get(Milestone_.PROGRESS_PERCENT),
                        Short.parseShort(p))));

        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(new OrderImpl(root.get(criteria.getOrder())));

        List<Milestone> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
