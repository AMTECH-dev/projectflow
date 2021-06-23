package io.amtech.projectflow;

import io.amtech.projectflow.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationManagerImpl implements AuthenticationManager {
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();
    static {
//        AUTHORITIES.add("ALL_ROLE_USER");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        authentication = new TokenFilter(authentication);

        if (authentication.getName().equals(authentication.getCredentials())) {
            return new UsernamePasswordAuthenticationToken(authentication.getName(),
                    authentication.getCredentials(), AUTHORITIES);
        }
        throw new BadCredentialsException("Bad Credentials");

//        return authentication;
    }
}
