package com.opengateway.validator;


import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.opengateway.validator.TestUtils.check;

@RunWith(SpringRunner.class)
public class DeprecatedDiscriminatorsTest {
    private static final String CONTRACT_LOCATION = "/contract_with_discriminator.yaml";

    @Test
    public void simpleValidation_postWithSingleObjects() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract_with_discriminator.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/oneOf")
                        .withBody("{\n" +
                                "  \"itemType\": \"Item1\",\n" +
                                "  \"stringField\": \"Cat\",\n" +
                                "  \"intField\": 23\n" +
                                "}")
                        .withContentType("application/json")
                        .build());

        check(report);
    }

    @Test
    public void simpleValidation_putWithArrayOfObjects() {
        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(CONTRACT_LOCATION)
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.PUT, "/oneOf")
                        .withBody("[{\n" +
                                "  \"itemType\": \"Item1\",\n" +
                                "  \"stringField\": \"Cat\",\n" +
                                "  \"intField\": 23\n" +
                                "}]")
                        .withContentType("application/json")
                        .build());

        check(report);
    }

    @Test
    public void simpleValidation_putWithSingleObjects() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract_with_discriminator.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.PUT, "/oneOf")
                        .withBody("{\n" +
                                "  \"itemType\": \"Item1\",\n" +
                                "  \"stringField\": \"Cat\",\n" +
                                "  \"intField\": 23\n" +
                                "}")
                        .withContentType("application/json")
                        .build());

        check(report);
    }

}
