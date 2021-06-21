package io.amtech.projectflow.util;

import java.time.Instant;
import java.util.Optional;

public class DateUtil {
    private DateUtil() {
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
