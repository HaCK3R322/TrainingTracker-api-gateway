package com.androsov.apigateway.filters;

import com.androsov.apigateway.adapters.AuthenticationServiceAdapter;
import com.androsov.apigateway.dto.UsernameAuthorities;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtFilter implements GatewayFilter {
    @Autowired
    private AuthenticationServiceAdapter authenticationServiceAdapter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Logger logger = Logger.getLogger(JwtFilter.class.getName());

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("Authorization");


        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);

            // Perform JWT validation logic here
            if (isJwtValid(jwt)) {
                logger.log(Level.INFO, "Validated request to " + request.getPath());

                UsernameAuthorities user = authenticationServiceAdapter.parse(jwt);

                // why cant i change headers? idk... i can only add them
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("Username", user.getUsername())
                        .header("Authorities", user.getAuthorities())
                        .header("UserId", String.valueOf(user.getId()))
                        .build();

                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();

                return chain.filter(modifiedExchange);
            }
        }

        logger.log(Level.INFO, "Got invalid request to " + request.getPath());

        // If JWT is missing or invalid, return forbidden status
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        return authenticationServiceAdapter.validate(jwt);
    }
}
