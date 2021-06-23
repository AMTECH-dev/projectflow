package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.general.PagedData;
import io.amtech.projectflow.app.general.SearchCriteria;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.Direction_;
import io.amtech.projectflow.repository.DirectionCustomRepository;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DirectionCustomRepositoryImpl implements DirectionCustomRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public PagedData<Direction> search(SearchCriteria criteria) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Direction> query = builder.createQuery(Direction.class);
        Root<Direction> root = query.from(Direction.class);
        List<Predicate> predicates = new ArrayList<>();

        criteria.getFilter(Direction_.NAME)
                .ifPresent(p -> predicates.add(builder.like(root.get(Direction_.NAME),
                        "%" + p.toLowerCase() + "%")));
        criteria.getFilter(Direction_.LEAD)
                .ifPresent(p -> predicates.add(builder.like(root.get(Direction_.LEAD),
                        "%" + p.toLowerCase() + "%")));

        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(new OrderImpl(root.get(criteria.getOrder())));

        List<Direction> result = em.createQuery(query)
                .setFirstResult(criteria.getOffset())
                .setMaxResults(criteria.getLimit())
                .getResultList();

        return new PagedData<>(result, criteria);
    }
}
