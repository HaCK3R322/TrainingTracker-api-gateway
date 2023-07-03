package com.androsov.apigateway.filters;

import com.androsov.apigateway.adapters.AuthenticationServiceAdapter;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtFilter implements GatewayFilter {
    @Autowired
    private AuthenticationServiceAdapter authenticationServiceAdapter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("Authorization");

        Logger logger = Logger.getLogger(JwtFilter.class.getName());

        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);

            // Perform JWT validation logic here
            if (isJwtValid(jwt)) {

                logger.log(Level.INFO, "Validated jwt request: VALID");

                return chain.filter(exchange);
            }
        }

        logger.log(Level.INFO, "Validated jwt request: INVALID");

        // If JWT is missing or invalid, return forbidden status
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        return authenticationServiceAdapter.validate(jwt);
    }
}
