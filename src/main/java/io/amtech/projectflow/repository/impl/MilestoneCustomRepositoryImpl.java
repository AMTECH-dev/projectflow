package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Milestone_;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.repository.MilestoneCustomRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static io.amtech.projectflow.repository.impl.CustomSearchUtil.parseOrderWithJoin;
import static io.amtech.projectflow.util.DateUtil.millisToInstant;

@Repository
public class MilestoneCustomRepositoryImpl implements MilestoneCustomRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public PagedData<Milestone> search(final long projectId, final SearchCriteria criteria) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Milestone> query = builder.createQuery(Milestone.class);
        final Root<Project> root = query.from(Project.class);
        final Join<Project, Milestone> joinMilestone = root.join(Project_.MILESTONES);
        final List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get(Project_.ID), projectId));

        filterQuery(criteria, builder, predicates, joinMilestone);

        query.select(joinMilestone)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrderWithJoin(joinMilestone, criteria.getOrder()));

        List<Milestone> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }

    private static void filterQuery(final SearchCriteria criteria,
                                    final CriteriaBuilder builder,
                                    final List<Predicate> predicates,
                                    final Join<Project, Milestone> joinMilestone) {
        final String fromRangeKey = "From";
        final String toRangeKey = "To";

        criteria.getFilter(Milestone_.NAME)
                .ifPresent(
                        p -> predicates.add(builder
                        .like(builder.lower(joinMilestone.get(Milestone_.NAME)),
                                "%" + p.toLowerCase() + "%"))
                );
        criteria.getFilter(Milestone_.PLANNED_START_DATE + fromRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .greaterThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_START_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PLANNED_START_DATE + toRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .lessThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_START_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PLANNED_FINISH_DATE + fromRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .greaterThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_FINISH_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PLANNED_FINISH_DATE + toRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .lessThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_FINISH_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_START_DATE + fromRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .greaterThanOrEqualTo(joinMilestone.get(Milestone_.FACT_START_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_START_DATE + toRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .lessThanOrEqualTo(joinMilestone.get(Milestone_.FACT_START_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_FINISH_DATE + fromRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .greaterThanOrEqualTo(joinMilestone.get(Milestone_.FACT_FINISH_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_FINISH_DATE + toRangeKey)
                .ifPresent(
                        p -> predicates.add(builder
                        .lessThanOrEqualTo(joinMilestone.get(Milestone_.FACT_FINISH_DATE),
                                millisToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PROGRESS_PERCENT)
                .ifPresent(
                        p -> predicates.add(builder
                        .equal(joinMilestone.get(Milestone_.PROGRESS_PERCENT),
                                Short.parseShort(p)))
                );
    }
}
