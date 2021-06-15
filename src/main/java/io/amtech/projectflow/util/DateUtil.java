package io.amtech.projectflow.util;

import java.time.Instant;
import java.util.Optional;

public class DateUtil {
    private DateUtil() {
    }

    public static Instant millisToInstant(Long millis) {
        return Optional.ofNullable(millis)
                .map(Instant::ofEpochMilli)
                .orElse(null);
    }

    public static Long instantToMillis(Instant instant) {
        return Optional.ofNullable(instant)
                .map(Instant::toEpochMilli)
                .orElse(null);
    }
}
