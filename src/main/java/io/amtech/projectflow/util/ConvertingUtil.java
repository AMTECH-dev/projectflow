package io.amtech.projectflow.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertingUtil {
    public static <T> String objToString(T value) {
        return Optional.ofNullable(value)
                .map(Object::toString)
                .orElse(null);
    }

    public static Instant secondToInstant(Long seconds) {
        return Optional.ofNullable(seconds)
                .map(Instant::ofEpochSecond)
                .orElse(null);
    }

    public static Long instantToSecond(Instant instant) {
        return Optional.ofNullable(instant)
                .map(Instant::getEpochSecond)
                .orElse(null);
    }
}
