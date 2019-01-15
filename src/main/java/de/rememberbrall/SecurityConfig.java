package de.rememberbrall;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;


@EnableWebFluxSecurity
public class SecurityConfig {

    static final String ADMIN = "ADMIN";
    private static final String PASSWORD_ADMIN = "{bcrypt}$2a$10$0p3peMKEhXSXEOS6D/Zk9epnMtGymuq4i6duWJFu6SuoW.kHVDEKO";
    private static final String USERNAME_ADMIN = "admin";

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User
                .withUsername(USERNAME_ADMIN)
                .password(PASSWORD_ADMIN)
                .roles(ADMIN)
                .build();
        return new MapReactiveUserDetailsService(admin);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.DELETE, "/entries").hasRole(ADMIN)
                .anyExchange().permitAll()
                .and().httpBasic()
                .and().formLogin()
                .and().build();
    }
}
