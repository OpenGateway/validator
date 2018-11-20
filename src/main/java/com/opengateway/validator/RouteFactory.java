package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
class RouteFactory {

    Function<PredicateSpec, Route.AsyncBuilder> getSimpleroute(String path, String contractPath, String uri) {
        return p -> p
                .path(path)
                .filters(f -> f.filter(new OpenApiValidatorFilterFactory().apply(new OpenApiValidatorFilterFactory.Config(contractPath))))
                .uri(uri);
    }
}
