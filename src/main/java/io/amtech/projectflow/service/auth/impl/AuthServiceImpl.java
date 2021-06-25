package io.amtech.projectflow.service.auth.impl;

import io.amtech.projectflow.app.exception.NotAuthenticationException;
import io.amtech.projectflow.domain.employee.AuthUser;
import io.amtech.projectflow.repository.AuthUserRepository;
import io.amtech.projectflow.service.auth.AuthService;
import io.amtech.projectflow.service.token.AuthDto;
import io.amtech.projectflow.service.token.TokenPayload;
import io.amtech.projectflow.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final TokenService tokenService;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public boolean validate(AuthDto dto) {
        AuthUser authUser = authUserRepository.findByLoginOrThrow(dto.getUsername());
        return passwordEncoder.matches(dto.getPassword(), authUser.getPassword());
    }

    @Override
    public Authentication authentication(String username, String token) {
        return new UsernamePasswordAuthenticationToken(username, token, Collections.singletonList(
                new SimpleGrantedAuthority("USER")));
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.replace("Bearer ", ""))
                .filter(tokenService::isValid)
                .map(token -> {
                    TokenPayload tokenPayload = tokenService.decode(token);
                    authUserRepository.findByLoginOrThrow(tokenPayload.getUsername());
                    return authentication(tokenPayload.getUsername(), token);
                })
                .orElseThrow(NotAuthenticationException::new);
    }
}
