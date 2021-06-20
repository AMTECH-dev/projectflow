package io.amtech.projectflow.util;

import io.amtech.projectflow.app.exception.InvalidOrderException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.OrderImpl;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderUtil {
    public static Order parseOrder(final Path<?> path, final String orderField) {
        try {
            return Optional.ofNullable(orderField)
                    .filter(x -> x.startsWith("-"))
                    .map(x -> new OrderImpl(path.get(x.substring(1)), false))
                    .orElseGet(() -> new OrderImpl(path.get(orderField)));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderException(orderField);
        }
    }
}
