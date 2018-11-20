package com.opengateway.validator;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
class RouteFactory {

    Function<PredicateSpec, Route.AsyncBuilder> getSimpleRoute(String path, String contractPath, String uri) {
        return p -> p
                .path(path)
                .filters(f -> f.filter(new OpenApiValidatorFilterFactory().apply(new OpenApiValidatorFilterFactory.Config(contractPath))))
                .uri(uri);
    }
}
