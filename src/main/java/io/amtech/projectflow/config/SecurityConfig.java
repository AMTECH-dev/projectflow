package io.amtech.projectflow.config;

import io.amtech.projectflow.filter.TokenFilter;
import io.amtech.projectflow.repository.AuthUserRepository;
import io.amtech.projectflow.service.auth.AuthService;
import io.amtech.projectflow.service.auth.impl.CustomAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthService authService;
    private final AuthUserRepository authUserRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        TokenFilter filter = tokenFilter();

        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(HttpMethod.POST, "/auth/**")
                .antMatchers("/swagger-ui", "/v2/api-docs");
    }

    @Bean
    @Override
    @SneakyThrows
    public AuthenticationManager authenticationManagerBean() {
        return new CustomAuthenticationManager(authUserRepository, authService);
    }

    @SneakyThrows
    public TokenFilter tokenFilter() {
        return new TokenFilter("/**", authService, authenticationManagerBean());
    }
}
