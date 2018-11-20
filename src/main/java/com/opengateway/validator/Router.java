package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
class Router {
    private final RouteFactory routeFactory;

    @Autowired
    public Router(RouteFactory routeFactory) {
        this.routeFactory = routeFactory;
    }

    @Bean
    RouteLocator getRouteLocator(RouteLocatorBuilder builder) {

        List<String> contracts = Arrays.asList("/simple_route_1.yaml", "/simple_route_2.yaml");

        List<OpenAPI> apis = contracts.stream().map(this::getOpenApi).collect(Collectors.toList());

        val routes = builder.routes();

        for (int i = 0; i < contracts.size(); i++) {
            for (String path : apis.get(i).getPaths().keySet()) {
                routes.route(routeFactory.getSimpleroute(path, contracts.get(i), apis.get(i).getServers().get(0).getUrl()));
            }
        }
        return routes.build();
    }

    public OpenAPI getOpenApi(String contractPath) {
        final OpenAPIParser openAPIParser = new OpenAPIParser();
        final ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);
        parseOptions.setResolveCombinators(false);

        SwaggerParseResult parseResult;
        try {
            // Try to load as a URL first, then as a content string if that fails
            parseResult = openAPIParser.readLocation(contractPath, null, parseOptions);
            if (parseResult == null || parseResult.getOpenAPI() == null) {
                parseResult = openAPIParser.readContents(contractPath, null, parseOptions);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (parseResult == null || parseResult.getOpenAPI() == null ||
                (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty())) {
            throw new RuntimeException();
        }

        return parseResult.getOpenAPI();
    }
}
