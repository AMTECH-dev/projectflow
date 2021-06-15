package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.UserPosition;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.Project_;
import io.amtech.projectflow.repository.ProjectCustomRepository;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
 public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {
    @PersistenceContext
    EntityManager entityManager;


    @Override
    public PagedData<Project> search(SearchCriteria criteria) {

        var builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = builder.createQuery(Project.class); //Select*from.., Project.class
        Root<Project> root = query.from(Project.class);
        Join<Project, Employee> joinWithEmployeeTable = root.join(Project_.PROJECT_LEAD);
        Join<Project, Direction> joinDirectionalTable = root.join(Project_.DIRECTION);

        List<Predicate> predicates = new ArrayList<>();

        criteria.getFilter(Project_.NAME)
                .ifPresent(v -> predicates.add(builder.like(root.get(Project_.name),
                        "%" + v.toLowerCase() + "%")));

        criteria.getFilter(Project_.PROJECT_LEAD)
                .ifPresent(v -> predicates.add(builder.like(builder.lower
                        (joinWithEmployeeTable.get(Project_.PROJECT_LEAD)), "%" + v.toLowerCase() + "%")));

  criteria.getFilter(Project_.DIRECTION)
                .ifPresent(v -> predicates.add(builder.like(builder.lower
                        (joinDirectionalTable.get(Project_.DIRECTION)), "%" + v.toLowerCase() + "%")));


        criteria.getFilter(Project_.CREATE_DATE + "From")
                .ifPresent(v -> predicates.add(builder.greaterThanOrEqualTo(root.get(Project_.CREATE_DATE),
                        Instant.ofEpochSecond(Long.parseLong(v)))));
        criteria.getFilter(Project_.CREATE_DATE + "To")
                .ifPresent(v -> predicates.add(builder.lessThanOrEqualTo(root.get(Project_.CREATE_DATE),
                        Instant.ofEpochSecond(Long.parseLong(v)))));


        criteria.getFilter(Project_.DESCRIPTION)
                .ifPresent(v -> predicates.add(builder.like(root.get(Project_.DIRECTION),
                        "%" + v.toLowerCase() + "%")));

        criteria.getFilter(Project_.PROJECT_STATUS)
                .ifPresent(v -> predicates.add(builder.like(root.get(Project_.PROJECT_STATUS),
                        "%" + v.toUpperCase() + "%")));



        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(root, criteria.getOrder()));


        List<Project> result = entityManager.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
    private Order parseOrder(Root<?> root, final String order) {
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
