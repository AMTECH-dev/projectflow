package io.amtech.projectflow;

import io.amtech.projectflow.util.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;

public class AuthenticationManagerImpl implements AuthenticationManager, AuthService {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (isPasswordCorrect(authentication.getPrincipal()) && authentication.getName().equals(authentication.getCredentials())) {
            return new UsernamePasswordAuthenticationToken(
                    authentication.getName(),
                    authentication.getCredentials(),
                    Collections.emptyList()
            );
        }
        throw new BadCredentialsException("Bad Credentials");
    }

    @Override
    public boolean isPasswordCorrect(Object dataWithPassword) {
        // ?..
        return false;
    }
}
