package io.amtech.projectflow;

import io.amtech.projectflow.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenFilter extends AbstractAuthenticationProcessingFilter {
    @Autowired
    private TokenService tokenService;

    protected TokenFilter(String defaultFilterProcessesUrl) {
        super("/**");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        Authentication authentication = tokenService.getAuthentication(request);
        authentication.setAuthenticated(false);

        boolean isAuthCorrect = tokenService.isValid(tokenService.resolveToken(request));
        authentication.setAuthenticated(isAuthCorrect);

        return authentication;
    }
}
