package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.Employee_;
import io.amtech.projectflow.domain.employee.UserPosition;
import io.amtech.projectflow.repository.EmployeeCustomRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static io.amtech.projectflow.util.SearchUtil.parseOrder;

@Repository
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public PagedData<Employee> search(SearchCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);
        List<Predicate> predicates = new ArrayList<>();
        criteria.getFilter(Employee_.NAME)
                .ifPresent(v -> predicates.add(builder.like(builder.lower(root.get(Employee_.NAME)),
                        "%" + v.toLowerCase() + "%")));
        criteria.getFilter(Employee_.EMAIL)
                .ifPresent(v -> predicates.add(builder.like(root.get(Employee_.EMAIL),
                        "%" + v.toLowerCase() + "%")));
        criteria.getFilter(Employee_.PHONE)
                .ifPresent(v -> predicates.add(builder.like(root.get(Employee_.PHONE),
                        "%" + v.toLowerCase() + "%")));
        criteria.getFilter(Employee_.POSITION)
                .ifPresent(v -> predicates.add(builder.equal(root.get(Employee_.POSITION),
                        UserPosition.getByName(v.toUpperCase()))));
        criteria.getFilter(Employee_.IS_FIRED)
                .ifPresent(v -> predicates.add(builder.equal(root.get(Employee_.IS_FIRED),
                        Boolean.valueOf(v))));
        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(root, criteria.getOrder()));

        List<Employee> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
