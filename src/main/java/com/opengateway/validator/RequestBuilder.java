package com.opengateway.validator;

import com.atlassian.oai.validator.model.SimpleRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;


public class RequestBuilder extends SimpleRequest.Builder {
    public RequestBuilder(ServerHttpRequest serverHttpRequest) {

        super(serverHttpRequest.getMethodValue(), serverHttpRequest.getPath().value());
        this.withHeaders(serverHttpRequest.getHeaders());
        this.withQueryParams(serverHttpRequest.getQueryParams());
    }

    private RequestBuilder withHeaders(HttpHeaders httpHeaders) {
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String headerName = entry.getKey();
            this.withHeader(headerName, entry.getValue());
        }
        return this;
    }

    private RequestBuilder withQueryParams(MultiValueMap<String, String> queryParameters) {
        for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
            String headerName = entry.getKey();
            this.withQueryParam(headerName, entry.getValue());
        }
        return this;

    }
}
