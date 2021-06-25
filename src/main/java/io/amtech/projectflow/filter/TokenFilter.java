package io.amtech.projectflow.filter;

import io.amtech.projectflow.service.auth.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthService authService;

    public TokenFilter(String urlRequireRequest, AuthService authService, AuthenticationManager authenticationManager) {
        super(urlRequireRequest);
        super.setAuthenticationManager(authenticationManager);
        super.setContinueChainBeforeSuccessfulAuthentication(true);
        this.authService = authService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication authenticate = super.getAuthenticationManager().authenticate(authService.getAuthentication(request));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
    }
}
