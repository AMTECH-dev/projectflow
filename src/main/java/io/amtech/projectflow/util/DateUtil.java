package io.amtech.projectflow.util;

import java.time.Instant;
import java.util.Optional;

public class DateUtil {
    private DateUtil() {
    }

    public static Instant secondsToInstant(Long seconds) {
        return Optional.ofNullable(seconds)
                .map(Instant::ofEpochSecond)
                .orElse(null);
    }

    public static Long instantToSeconds(Instant instant) {
        return Optional.ofNullable(instant)
                .map(Instant::getEpochSecond)
                .orElse(null);
    }
}
