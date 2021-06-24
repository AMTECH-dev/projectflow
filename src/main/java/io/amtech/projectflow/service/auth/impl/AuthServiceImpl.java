package io.amtech.projectflow.service.auth.impl;

import io.amtech.projectflow.app.exception.NotAuthenticationException;
import io.amtech.projectflow.domain.employee.AuthUser;
import io.amtech.projectflow.repository.AuthUserRepository;
import io.amtech.projectflow.service.auth.AuthService;
import io.amtech.projectflow.service.token.AuthDto;
import io.amtech.projectflow.service.token.TokenPayload;
import io.amtech.projectflow.service.token.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final TokenService tokenService;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public AuthServiceImpl(TokenService tokenService, AuthUserRepository authUserRepository) {
        this.tokenService = tokenService;
        this.authUserRepository = authUserRepository;
    }

    @Override
    public boolean isPasswordCorrect(String userPass, String passwordFromDb) {
        return passwordEncoder.matches(userPass, passwordFromDb);
    }

    @Override
    public Authentication authentication(AuthDto dto) {
        AuthUser user = authUserRepository.findByLoginOrThrow(dto.getUsername());
        if (!isPasswordCorrect(dto.getPassword(), user.getPassword())) {
            throw new NotAuthenticationException();
        }
        return new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(), Collections.emptyList());
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.replace("Bearer ", ""))
                .filter(tokenService::isValid)
                .map(token -> {
                    TokenPayload tokenPayload = tokenService.decode(token);
                    AuthUser user = authUserRepository.findByLoginOrThrow(tokenPayload.getUsername());
                    return new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(),
                            Collections.emptyList());
                })
                .orElseThrow(NotAuthenticationException::new);
    }
}
