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

    private static final String ADMIN_ROLE = "ADMIN_ROLE";

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("admin")
                .password("{bcrypt}$2a$10$0p3peMKEhXSXEOS6D/Zk9epnMtGymuq4i6duWJFu6SuoW.kHVDEKO")
                .roles(ADMIN_ROLE)
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.DELETE, "/entries").hasRole(ADMIN_ROLE)
                .anyExchange().permitAll()
                .and().httpBasic()
                .and().formLogin()
                .and().build();
    }
}
