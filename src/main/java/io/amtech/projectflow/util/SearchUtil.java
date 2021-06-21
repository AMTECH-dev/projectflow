package io.amtech.projectflow.util;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.OrderImpl;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchUtil {
    public static final String FROM_DATE_KEY = "From";
    public static final String TO_DATE_KEY = "To";

    public static Order parseOrder(final Path<?> path, final String orderField) {
        Order order;
        try {
            order = Optional.ofNullable(orderField)
                    .filter(f -> f.startsWith("-"))
                    .map(f -> new OrderImpl(path.get(f.substring(1)), false))
                    .orElseGet(() -> new OrderImpl(path.get(orderField)));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(orderField);
        }
        return order;
    }
}
