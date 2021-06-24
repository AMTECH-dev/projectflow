package io.amtech.projectflow.service.auth;

import io.amtech.projectflow.service.token.AuthDto;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    boolean isPasswordCorrect(String userPass, String passwordFromDb);
    Authentication authentication(AuthDto dto);
    Authentication getAuthentication(HttpServletRequest request);
}
