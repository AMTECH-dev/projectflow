package io.amtech.projectflow.repository.impl;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import org.hibernate.query.criteria.internal.OrderImpl;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

class CustomSearchUtil {

    private CustomSearchUtil(){
    }

    static Order parseOrder(final Root<?> root, final String order) {
        try {
            if (order.startsWith("-"))
                return new OrderImpl(root.get(order.substring(1)), false);

            return new OrderImpl(root.get(order));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(order);
        }
    }

    static Order parseOrderWithJoinQuery(final Join<?, ?> root, final String order) {
        try {
            if (order.startsWith("-"))
                return new OrderImpl(root.get(order.substring(1)), false);

            return new OrderImpl(root.get(order));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(order);
        }
    }
}
