package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.repository.EmployeeCustomRepository;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public PagedData<Employee> search(SearchCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);
        List<Predicate> predicates = new ArrayList<>();
        criteria.getFilter("name")
                .ifPresent(v -> predicates.add(builder.like(root.get("name"), "%" + v + "%")));
        criteria.getFilter("name")
                .ifPresent(v -> predicates.add(builder.like(root.get("email"), "%" + v + "%")));
        criteria.getFilter("email")
                .ifPresent(v -> predicates.add(builder.like(root.get("phone"), "%" + v + "%")));
        criteria.getFilter("position")
                .ifPresent(v -> predicates.add(builder.equal(root.get("position"), v)));
        criteria.getFilter("fired")
                .ifPresent(v -> predicates.add(builder.equal(root.get("isFired"), v)));
        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(root, criteria.getOrder()));

        List<Employee> result = em.createQuery(query).getResultList();

        return new PagedData<>(result, criteria);
    }

    private Order parseOrder(Root<?> root, final String order) {
        if (order.startsWith("-")) {
            return new OrderImpl(root.get(order.substring(1)), false);
        }

        return new OrderImpl(root.get(order));
    }
}
