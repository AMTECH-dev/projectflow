package io.amtech.projectflow.rest.general;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ErrorMessage {
    private String message;
    private Map<String, String> errors = new HashMap<>();
}
