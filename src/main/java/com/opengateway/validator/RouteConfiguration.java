package com.opengateway.validator;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RouteConfiguration {
    private final RouteFactory routeFactory;
    private final ApplicationProperties applicationProperties;

    @Bean
    RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
        val contracts = applicationProperties.getContracts();
        val apis = contracts.stream().map(this::getOpenApi).collect(Collectors.toList());
        val routes = builder.routes();
        for (int i = 0; i < contracts.size(); i++) {
            val contract = contracts.get(i);
            val uri = apis.get(i).getServers().get(0).getUrl();
            for (String path : apis.get(i).getPaths().keySet()) {
                routes.route(routeFactory.getSimpleRoute(path, contract, uri));
            }
        }
        return routes.build();
    }

    private OpenAPI getOpenApi(String contractPath) {
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
