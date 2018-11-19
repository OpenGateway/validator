package com.opengateway.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Router {
    private final RouteFactory routeFactory;

    @Autowired
    public Router(RouteFactory routeFactory) {
        this.routeFactory = routeFactory;
    }

    @Bean
    RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(routeFactory.getHelloWorld())
                .route(routeFactory.getStub())
                .build();
    }
}
