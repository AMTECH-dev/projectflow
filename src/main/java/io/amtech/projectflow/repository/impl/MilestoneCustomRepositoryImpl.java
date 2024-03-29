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

import static io.amtech.projectflow.util.ConvertingUtil.secondToInstant;
import static io.amtech.projectflow.util.SearchUtil.*;

@Repository
public class MilestoneCustomRepositoryImpl implements MilestoneCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public PagedData<Milestone> search(final long projectId, final SearchCriteria criteria) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Milestone> query = builder.createQuery(Milestone.class);
        final Root<Project> root = query.from(Project.class);
        final Join<Project, Milestone> joinMilestone = root.join(Project_.MILESTONES);
        final List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get(Project_.ID), projectId));

        criteria.getFilter(Milestone_.NAME)
                .ifPresent(
                        p -> predicates.add(builder
                                .like(builder.lower(joinMilestone.get(Milestone_.NAME)),
                                        "%" + p.toLowerCase() + "%"))
                );
        criteria.getFilter(Milestone_.PLANNED_START_DATE + FROM_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .greaterThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_START_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PLANNED_START_DATE + TO_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .lessThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_START_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PLANNED_FINISH_DATE + FROM_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .greaterThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_FINISH_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PLANNED_FINISH_DATE + TO_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .lessThanOrEqualTo(joinMilestone.get(Milestone_.PLANNED_FINISH_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_START_DATE + FROM_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .greaterThanOrEqualTo(joinMilestone.get(Milestone_.FACT_START_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_START_DATE + TO_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .lessThanOrEqualTo(joinMilestone.get(Milestone_.FACT_START_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_FINISH_DATE + FROM_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .greaterThanOrEqualTo(joinMilestone.get(Milestone_.FACT_FINISH_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.FACT_FINISH_DATE + TO_DATE_KEY)
                .ifPresent(
                        p -> predicates.add(builder
                                .lessThanOrEqualTo(joinMilestone.get(Milestone_.FACT_FINISH_DATE),
                                        secondToInstant(Long.parseLong(p))))
                );
        criteria.getFilter(Milestone_.PROGRESS_PERCENT)
                .ifPresent(
                        p -> predicates.add(builder
                                .equal(joinMilestone.get(Milestone_.PROGRESS_PERCENT),
                                        Short.parseShort(p)))
                );

        query.select(joinMilestone)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(joinMilestone, criteria.getOrder()));

        List<Milestone> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
