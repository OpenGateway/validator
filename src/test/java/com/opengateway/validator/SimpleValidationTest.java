package com.opengateway.validator;



import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@RunWith(SpringRunner.class)
public class SimpleValidationTest {


    @Test
    public void simpleValidation() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(new Request() {

            @Override
            public String getPath() {
                return "/pets";
            }


            @Override
            public Method getMethod() {
                return Method.POST;
            }


            @Override
            public Optional<String> getBody() {
                return Optional.of("{\"name\": \"kk\"}");
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

        assertTrue("Found Unexpected errors", !report.hasErrors());

    }

    @Test
    public void simpleRouteValidation() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/simple_route.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(new Request() {

            @Override
            public String getPath() {
                return "/simpleRoute";
            }


            @Override
            public Method getMethod() {
                return Method.POST;
            }


            @Override
            public Optional<String> getBody() {
                return Optional.of("{\"simpleString\": \"kdjsh\", \"simpleInt\": 123}");
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

        assertTrue("Found Unexpected errors", !report.hasErrors());

    }

}

