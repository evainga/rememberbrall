package de.rememberbrall;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

// This class is a workaround for this issue: https://github.com/spring-projects/spring-boot/issues/9785
@Component
public class CustomWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().equals("/")) {
            ServerHttpRequest mutatedHttpRequest = exchange.getRequest().mutate().path("/docs/index.html").build();
            ServerWebExchange serverWebExchange = exchange.mutate().request(mutatedHttpRequest).build();
            Mono<Void> filter = chain.filter(serverWebExchange);
            return filter;
        }

        return chain.filter(exchange);
    }
}