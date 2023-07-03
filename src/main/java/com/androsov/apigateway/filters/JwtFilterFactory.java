package com.androsov.apigateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtFilterFactory extends AbstractGatewayFilterFactory<JwtFilterFactory.Config> {

    @Autowired
    private JwtFilter jwtFilter;

    public JwtFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return jwtFilter;
    }

    public static class Config {
        // void config, I need nothing
    }
}

