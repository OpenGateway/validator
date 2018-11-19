package com.opengateway.validator;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
class RouteFactory {

    Function<PredicateSpec, Route.AsyncBuilder> getHelloWorld() {
        return p -> p
                .path("/get")
                .filters(f -> f.addRequestHeader("Hello", "World"))
                .uri("http://httpbin.org:80");
    }

    Function<PredicateSpec, Route.AsyncBuilder> getStub() {
        return p -> p
                .path("/stub")
                .uri("forward:/stub");
    }
}
