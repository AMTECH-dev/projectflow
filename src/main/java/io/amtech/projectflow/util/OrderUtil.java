package io.amtech.projectflow.util;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import org.hibernate.query.criteria.internal.OrderImpl;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

public class OrderUtil {
    private OrderUtil() {
    }

    public static Order parseOrder(final Path<?> path, final String orderField) {
        try {
            if (orderField.startsWith("-"))
                return new OrderImpl(path.get(orderField.substring(1)), false);

            return new OrderImpl(path.get(orderField));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(orderField);
        }
    }
}

