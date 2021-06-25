package io.amtech.projectflow.service.token;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthDto {
    private String username;
    private String password;
}
