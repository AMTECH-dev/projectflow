package io.amtech.projectflow.service.token;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TokenRefreshDto {
    @NotBlank
    private String refreshToken;
}
