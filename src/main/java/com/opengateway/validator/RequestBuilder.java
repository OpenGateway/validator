package com.opengateway.validator;

import com.atlassian.oai.validator.model.SimpleRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;


public class RequestBuilder extends SimpleRequest.Builder {
    public RequestBuilder(String method, String path) {
        super(method, path);
    }

    public RequestBuilder withHeaders(HttpHeaders httpHeaders) {
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String headerName = entry.getKey();
            this.withHeader(headerName, entry.getValue());
        }
        return this;
    }

    public RequestBuilder withQueryParams(MultiValueMap<String, String> queryParameters) {
        for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
            String headerName = entry.getKey();
            this.withQueryParam(headerName, entry.getValue());
        }
        return this;

    }
}
