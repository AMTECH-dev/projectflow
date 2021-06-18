package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectComment;
import io.amtech.projectflow.domain.project.ProjectComment_;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.repository.ProjectCommentCustomRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static io.amtech.projectflow.util.DateUtil.secondsToInstant;
import static io.amtech.projectflow.util.OrderUtil.parseOrder;

@Repository
public class ProjectCommentCustomRepositoryImpl implements ProjectCommentCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public PagedData<ProjectComment> search(long projectId, SearchCriteria criteria) {
        var builder = em.getCriteriaBuilder();
        CriteriaQuery<ProjectComment> query = builder.createQuery(ProjectComment.class);
        Root<Project> root = query.from(Project.class);
        Join<Project, ProjectComment> join = root.join(Project_.projectComments);
        List<Predicate> predicates = new ArrayList<>() {{
            add(builder.equal(root.get(Project_.ID), projectId));
        }};
        criteria.getFilter(ProjectComment_.LOGIN)
                .ifPresent(v -> predicates.add(builder.like(builder.lower(join.get(ProjectComment_.LOGIN)), "%" + v.toLowerCase() + "%")));
        criteria.getFilter(ProjectComment_.MESSAGE)
                .ifPresent(v -> predicates.add(builder.like(join.get(ProjectComment_.MESSAGE), "%" + v.toLowerCase() + "%")));
        criteria.getFilter(ProjectComment_.CREATE_DATE + "From")
                .ifPresent(v -> predicates.add(builder.greaterThanOrEqualTo(join.get(ProjectComment_.CREATE_DATE),
                        secondsToInstant(Long.parseLong(v)))));
        criteria.getFilter(ProjectComment_.CREATE_DATE + "To")
                .ifPresent(v -> predicates.add(builder.lessThanOrEqualTo(join.get(ProjectComment_.CREATE_DATE),
                        secondsToInstant(Long.parseLong(v)))));

        query.select(join)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(join, criteria.getOrder()));

        List<ProjectComment> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
