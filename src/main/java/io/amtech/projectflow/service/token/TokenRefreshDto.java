package io.amtech.projectflow.service.token;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TokenRefreshDto {
    @NotNull
    private String refreshToken;
}
