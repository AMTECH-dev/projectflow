package io.amtech.projectflow.util;

import java.util.Optional;

public class ConvertingUtil {
    private ConvertingUtil(){
    }

    public static <T> String objToString(T value) {
        return Optional.ofNullable(value)
                .map(Object::toString)
                .orElse(null);
    }
}

