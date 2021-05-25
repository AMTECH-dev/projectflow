package io.amtech.projectflow.app.general;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Meta {
    private final int limit;
    private final int offset;
}
