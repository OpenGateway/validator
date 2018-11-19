package com.opengateway.validator;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.EMPTY_LIST;

public class OpenApiValidatorFilter extends AbstractGatewayFilterFactory<OpenApiValidatorFilter.OpenApiValidatorConfig> {

    private static final Logger log = LoggerFactory.getLogger(OpenApiValidatorFilter.class);

    @Override
    public GatewayFilter apply(OpenApiValidatorConfig config) {
        OpenApiInteractionValidator validator = config.getValidator();

        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            log.info("Path: {}", path);
            Request.Method method = Request.Method.valueOf(request.getMethodValue());
            log.info("Method: {}", method);
            final String body;
            ValidationReport report = null;
            try {
                body = new DefaultServerRequest(exchange).bodyToMono(String.class).toFuture().get();

                report = validator.validateRequest(new Request() {

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

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (report == null || report.hasErrors()) {

                exchange.getResponse().setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
                return exchange.getResponse().setComplete();

            }
            return chain.filter(exchange);
        };
        
    }

    public static class OpenApiValidatorConfig {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract.yaml")
                .build();



        private Long maxSize = 5000000L;

        public OpenApiValidatorConfig setMaxSize(Long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Long getMaxSize() {
            return maxSize;
        }

        public OpenApiInteractionValidator getValidator() {
            return validator;
        }
    }
}
