package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.Direction_;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.Employee_;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.repository.ProjectCustomRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static io.amtech.projectflow.util.ConvertingUtil.secondToInstant;
import static io.amtech.projectflow.util.SearchUtil.*;

@Repository
public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public PagedData<Project> search(SearchCriteria criteria) {

        var builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = builder.createQuery(Project.class);
        Root<Project> root = query.from(Project.class);
        Join<Project, Employee> joinWithEmployeeTable = root.join(Project_.PROJECT_LEAD);
        Join<Project, Direction> joinDirectionalTable = root.join(Project_.DIRECTION);

        List<Predicate> predicates = new ArrayList<>();

        criteria.getFilter(Project_.NAME)
                .ifPresent(v -> predicates.add(builder.like(root.get(Project_.name),
                        "%" + v.toLowerCase() + "%")));

        criteria.getFilter(Project_.PROJECT_LEAD)
                .ifPresent(v -> predicates.add(builder.like(builder.lower
                        (joinWithEmployeeTable.get(Employee_.NAME)), "%" + v.toLowerCase() + "%")));

        criteria.getFilter(Project_.DIRECTION)
                .ifPresent(v -> predicates.add(builder.like(builder.lower
                        (joinDirectionalTable.get(Direction_.NAME)), "%" + v.toLowerCase() + "%")));


        criteria.getFilter(Project_.CREATE_DATE + FROM_DATE_KEY)
                .ifPresent(v -> predicates.add(builder.greaterThanOrEqualTo(root.get(Project_.CREATE_DATE),
                        secondToInstant(Long.parseLong(v)))));
        criteria.getFilter(Project_.CREATE_DATE + TO_DATE_KEY)
                .ifPresent(v -> predicates.add(builder.lessThanOrEqualTo(root.get(Project_.CREATE_DATE),
                        secondToInstant(Long.parseLong(v)))));


        criteria.getFilter(Project_.DESCRIPTION)
                .ifPresent(v -> predicates.add(builder.like(root.get(Project_.DIRECTION),
                        "%" + v.toLowerCase() + "%")));

        criteria.getFilter(Project_.PROJECT_STATUS)
                .ifPresent(v -> predicates.add(builder.equal(root.get(Project_.PROJECT_STATUS),
                        ProjectStatus.getByName(v.toUpperCase()))));


        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(root, criteria.getOrder()));


        List<Project> result = entityManager.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
