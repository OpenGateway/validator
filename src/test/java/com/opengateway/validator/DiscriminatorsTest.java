package com.opengateway.validator;


import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
public class DiscriminatorsTest {

    @Test
    public void simpleValidation_putWithArrayOfObjects() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract_with_discriminator.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(new Request() {

            @Override
            public String getPath() {
                return "/oneOf";
            }


            @Override
            public Method getMethod() {
                return Method.PUT;
            }


            @Override
            public Optional<String> getBody() {
                return Optional.of("[{\n" +
                        "  \"springField\": \"Cat\",\n" +
                        "  \"intField\": 23\n" +
                        "}]");
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
    public void simpleValidation_putWithSingleObjects() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract_with_discriminator.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(new Request() {

            @Override
            public String getPath() {
                return "/oneOf";
            }


            @Override
            public Method getMethod() {
                return Method.PUT;
            }


            @Override
            public Optional<String> getBody() {
                return Optional.of("{\n" +
                        "  \"springField\": \"Cat\",\n" +
                        "  \"intField\": 23\n" +
                        "}");
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
    public void simpleValidation_postWithSingleObjects() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract_with_discriminator.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(new Request() {

            @Override
            public String getPath() {
                return "/oneOf";
            }


            @Override
            public Method getMethod() {
                return Method.POST;
            }


            @Override
            public Optional<String> getBody() {
                return Optional.of("{\n" +
                        "  \"springField\": \"Cat\",\n" +
                        "  \"intField\": 23\n" +
                        "}");
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
