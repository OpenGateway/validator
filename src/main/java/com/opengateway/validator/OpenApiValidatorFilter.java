package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.ValidationReport;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OpenApiValidatorFilter extends AbstractGatewayFilterFactory<OpenApiValidatorFilter.OpenApiValidatorConfig> {
    private static final Logger log = LoggerFactory.getLogger(OpenApiValidatorFilter.class);

    @Override
    public GatewayFilter apply(OpenApiValidatorConfig config) {
        OpenApiInteractionValidator validator = config.getValidator();

        return (exchange, chain) -> {
            val request = new DefaultServerRequest(exchange);
            Mono<String> modifiedBody = request
                    .bodyToMono(String.class)
                    .map(b -> new UnvalidatedRequest(request, b))
                    .flatMap(r -> {
                        final ValidationReport report = validator.validateRequest(r);
                        if (report.hasErrors()) {
                            log.info("Report: {}", report);
                            log.info("UNPROCESSABLE ENTITY");
                            val response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
                            response.setComplete();
                        }
                        log.info("ALL GOOD");
                        return Mono.justOrEmpty(r.getBody());
                    });
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());


            // if the body is changing content types, set it here, to the bodyInserter will know about it

            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage,  new BodyInserterContext())
                    // .log("modify_request", Level.INFO)
                    .then(Mono.defer(() -> {
                        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return outputMessage.getBody();
                            }
                        };
                        return chain.filter(exchange.mutate().request(decorator).build());
                    }));
        };

    }

    public static class OpenApiValidatorConfig {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/simple_route.yaml")
                .build();


        public OpenApiInteractionValidator getValidator() {
            return validator;
        }
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public static class UnprocException extends RuntimeException {
        public UnprocException(String message) {
            super(message);
        }
    }
}
