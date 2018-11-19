package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;

public class OpenApiValidatorFilter extends AbstractGatewayFilterFactory<OpenApiValidatorFilter.OpenApiValidatorConfig> {

    private static final Logger log = LoggerFactory.getLogger(OpenApiValidatorFilter.class);
//https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-core/src/main/java/org/springframework/cloud/gateway/filter/factory/rewrite/ModifyRequestBodyGatewayFilterFactory.java
    @Override
    public GatewayFilter apply(OpenApiValidatorConfig config) {
        OpenApiInteractionValidator validator = config.getValidator();


        return (exchange, chain) -> {
            ServerRequest serverRequest = new DefaultServerRequest(exchange);
            //TODO: flux or mono

            ServerHttpRequest request = exchange.getRequest();

            String path = request.getPath().value();
            log.info("Path: {}", path);
            Request.Method method = Request.Method.valueOf(request.getMethodValue());
            log.info("Method: {}", method);

            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {


                log.info("Body: {}", body);

                final ValidationReport report = validator.validateRequest(new Request() {

                    @Override
                    public String getPath() {
                        return path;
                    }

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public Optional<String> getBody() {
                        return Optional.ofNullable(body);
                    }


                    @Override
                    public Collection<String> getQueryParameters() {
                        return EMPTY_LIST;
                    }


                    @Override
                    public Collection<String> getQueryParameterValues(String s) {
                        return EMPTY_LIST;
                    }


                    @Override
                    public Map<String, Collection<String>> getHeaders() {
                        return Collections.singletonMap("Content-Type", Collections.singleton("application/json"));
                    }

                    @Override
                    public Collection<String> getHeaderValues(String s) {
                        return getHeaders().getOrDefault(s, EMPTY_LIST);
                    }
                });
                if (report == null || report.hasErrors()) {
                    System.out.println(report);
                    exchange.getResponse().setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);

                    System.out.println("UNPROCESSABLE ENTITY");
                    //return exchange.getResponse().setComplete();

                } else {
                    System.out.println("ALL GOOD");
                }

                return Mono.just(body);

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
                            public HttpHeaders getHeaders() {
                                long contentLength = headers.getContentLength();
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.putAll(super.getHeaders());
                                if (contentLength > 0) {
                                    httpHeaders.setContentLength(contentLength);
                                } else {
                                    // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
                                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                                }
                                return httpHeaders;
                            }

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
}
