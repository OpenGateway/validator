package com.opengateway.validator;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
class RouteFactory {

    Function<PredicateSpec, Route.AsyncBuilder> getSimpleroute() {
        return p -> p
                .path("/post")
                .filters(f -> f.filter(new OpenApiValidatorFilterFactory().apply(new OpenApiValidatorFilterFactory.Config("/simple_route.yaml"))))
                .uri("http://httpbin.org:80");
    }

    Function<PredicateSpec, Route.AsyncBuilder> getStub() {
        return p -> p
                .path("/stub")
                .uri("forward:/stub");
    }
}
