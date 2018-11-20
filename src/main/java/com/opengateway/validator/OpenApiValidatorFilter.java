package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.ValidationReport;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

public class OpenApiValidatorFilter extends AbstractGatewayFilterFactory<OpenApiValidatorFilter.OpenApiValidatorConfig> {
    private static final Logger log = LoggerFactory.getLogger(OpenApiValidatorFilter.class);

    @Override
    public GatewayFilter apply(OpenApiValidatorConfig config) {
        OpenApiInteractionValidator validator = config.getValidator();

        return (exchange, chain) -> {
            val request = new DefaultServerRequest(exchange);
            return request
                    .bodyToMono(String.class)
                    .map(b -> new UnvalidatedRequest(request, b))
                    .flatMap(r -> {
                        final ValidationReport report = validator.validateRequest(r);
                        return report.hasErrors()
                                ? writeResponse(exchange, report)
                                : rewriteRequest(exchange, chain, Mono.justOrEmpty(r.getBody()));
                    });
        };
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, ValidationReport report) {
        log.info("Report: {}", report);
        log.info("UNPROCESSABLE ENTITY");
        val response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
        return response.setComplete();
    }

    private Mono<Void> rewriteRequest(ServerWebExchange exchange, GatewayFilterChain chain, Mono<String> body) {
        log.info("ALL GOOD");
        val headers = new HttpHeaders(exchange.getRequest().getHeaders());
        val outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return BodyInserters.fromPublisher(body, String.class)
                .insert(outputMessage,  new BodyInserterContext())
                .then(Mono.defer(() -> {
                    val decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Nonnull
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }));
    }

    public static class OpenApiValidatorConfig {
        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/simple_route.yaml")
                .build();

        public OpenApiInteractionValidator getValidator() {
            return validator;
        }
    }
}
