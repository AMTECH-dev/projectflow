package io.amtech.projectflow.service.token;

import lombok.Getter;
import lombok.Value;

import java.time.Instant;

@Value
@Getter
public class TokenPayload {
    String username;
    Instant expireAt;
}
