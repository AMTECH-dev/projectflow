package io.amtech.projectflow.util;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import org.hibernate.query.criteria.internal.OrderImpl;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

public class OrderUtil {
    private OrderUtil() {
    }

    public static Order parseOrder(final Path<?> root, final String order) {
        try {
            if (order.startsWith("-"))
                return new OrderImpl(root.get(order.substring(1)), false);

            return new OrderImpl(root.get(order));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(order);
        }
    }
}
