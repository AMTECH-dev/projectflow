package io.amtech.projectflow.service.auth.impl;

import io.amtech.projectflow.repository.AuthUserRepository;
import io.amtech.projectflow.service.auth.AuthService;
import io.amtech.projectflow.service.token.TokenPayload;
import io.amtech.projectflow.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class CustomAuthenticationManager implements AuthenticationManager {
    private final AuthUserRepository authUserRepository;
    private final AuthService authService;
    private final TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        TokenPayload decode = tokenService.decode(token);
        authUserRepository.findByLoginOrThrow(decode.getUsername());
        if (tokenService.isValid(token)) return authService.authentication(decode.getUsername(), token);
        authentication.setAuthenticated(false);
        return authentication;
    }
}
