package io.amtech.projectflow.util;

import java.util.Objects;
import java.util.Optional;

public class ConvertingUtil {
    private ConvertingUtil(){
    }

    public static String objToString(Object o) {
        return Optional.ofNullable(o).map(Objects::toString).orElse(null);
    }
}
