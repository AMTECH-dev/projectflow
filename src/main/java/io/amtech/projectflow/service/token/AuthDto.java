package io.amtech.projectflow.service.token;

import lombok.Value;

@Value
public class AuthDto {
    String username;
    String password;
}
