package io.amtech.projectflow.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertingUtil {
    public static <T> String objToString(T value) {
        return Optional.ofNullable(value)
                .map(Object::toString)
                .orElse(null);
    }
}
