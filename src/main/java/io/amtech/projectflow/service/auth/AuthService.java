package io.amtech.projectflow.service.auth;

import io.amtech.projectflow.service.token.AuthDto;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    boolean validate(AuthDto dto);
    Authentication authentication(String username, String token);
    Authentication getAuthentication(HttpServletRequest request);
}
