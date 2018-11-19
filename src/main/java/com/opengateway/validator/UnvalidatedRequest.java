package com.opengateway.validator;

import com.atlassian.oai.validator.model.Request;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.EMPTY_LIST;

public class UnvalidatedRequest implements Request {
    private static final Logger log = LoggerFactory.getLogger(UnvalidatedRequest.class);
    private final String path;
    private final Method method;
    private final String body;

    public UnvalidatedRequest(ServerRequest request, String body) {
        path = getPath(request);
        method = getMethod(request);
        this.body = getBody(body);
    }

    private static String getPath(ServerRequest request) {
        String path = request.uri().getPath();
        log.info("Path: {}", path);
        return path;
    }

    private static Method getMethod(ServerRequest request) {
        HttpMethod method = request.method();
        log.info("Method: {}", method);
        return Request.Method.valueOf(request.methodName());
    }

    private String getBody(String body) {
        log.info("Body: {}", body);
        return body;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Request.Method getMethod() {
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
}
