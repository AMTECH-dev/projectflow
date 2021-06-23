package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.Direction_;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.Employee_;
import io.amtech.projectflow.repository.DirectionCustomRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static io.amtech.projectflow.util.SearchUtil.parseOrder;

@Repository
public class DirectionCustomRepositoryImpl implements DirectionCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public PagedData<Direction> search(SearchCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Direction> query = builder.createQuery(Direction.class);
        Root<Direction> root = query.from(Direction.class);
        List<Predicate> predicates = new ArrayList<>();
        Join<Direction, Employee> employeeJoin = root.join(Direction_.lead);

        criteria.getFilter(Direction_.NAME)
                .ifPresent(p -> predicates.add(builder.like(builder.lower(root.get(Direction_.NAME)),
                        "%" + p.toLowerCase() + "%")));
        criteria.getFilter("leadName")
                .ifPresent(p -> predicates.add(builder.like(builder.lower(employeeJoin.get(Employee_.NAME)),
                        "%" + p.toLowerCase() + "%")));
        criteria.getFilter("leadId")
                .ifPresent(p -> predicates.add(builder.equal(employeeJoin.get(Employee_.ID),
                        Long.parseLong(p))));

        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(parseOrder(root, criteria.getOrder()));

        List<Direction> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
