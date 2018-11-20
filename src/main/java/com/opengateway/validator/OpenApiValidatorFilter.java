package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OpenApiValidatorFilter extends AbstractGatewayFilterFactory<OpenApiValidatorFilter.OpenApiValidatorConfig> {

    private static final Logger log = LoggerFactory.getLogger(OpenApiValidatorFilter.class);
//https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-core/src/main/java/org/springframework/cloud/gateway/filter/factory/rewrite/ModifyRequestBodyGatewayFilterFactory.java

    @Override
    public GatewayFilter apply(OpenApiValidatorConfig config) {


        return (exchange, chain) -> parseRequest(exchange, chain, config);

    }

    private Mono<? extends String> validate(OpenApiInteractionValidator validator, ServerHttpRequest serverHttpRequest, String body, ValidationReportHolder holder) {
        Request request = new RequestBuilder(serverHttpRequest).withBody(body).build();
        ValidationReport report = validator.validateRequest(request);
        holder.add(report);

        return Mono.just(body);
    }

    private Mono parseRequest(ServerWebExchange exchange, GatewayFilterChain chain, OpenApiValidatorConfig config) {
        OpenApiInteractionValidator validator = config.getValidator();

        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ValidationReportHolder holder = new ValidationReportHolder();
        Mono<String> modifiedBody = new DefaultServerRequest(exchange)
            .bodyToMono(String.class)
            .flatMap(body ->
                    validate(validator, serverHttpRequest, body, holder)
            );

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        CachedBodyOutputMessage cachedBody = new CachedBodyOutputMessage(exchange, exchange.getRequest().getHeaders());

        return bodyInserter.insert(cachedBody,  new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedBody.getBody();
                        }
                    };

                    if (holder.get().hasErrors()) {
                        System.out.println(holder.get().getMessages());

                        exchange.getResponse().setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
                        return exchange.getResponse().setComplete();
                    }

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
