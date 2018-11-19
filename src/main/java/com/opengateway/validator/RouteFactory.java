package com.opengateway.validator;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
class RouteFactory {

    Function<PredicateSpec, Route.AsyncBuilder> getSimpleroute() {
        return p -> p
                .path("/post")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f.filter(new OpenApiValidatorFilter().apply(new OpenApiValidatorFilter.OpenApiValidatorConfig())))
                .uri("http://httpbin.org:80");
    }

    Function<PredicateSpec, Route.AsyncBuilder> getStub() {
        return p -> p
                .path("/stub")
                .uri("forward:/stub");
    }
}
