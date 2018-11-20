package com.opengateway.validator;


import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.LevelResolverFactory;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.opengateway.validator.TestUtils.check;

@RunWith(SpringRunner.class)
public class PolymorphismTest {
    private static final String CONTRACT_LOCATION = "/petstore-debugging.yaml";

    @Test
    public void simpleValidation() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(CONTRACT_LOCATION)
                .withLevelResolver(LevelResolverFactory.withAdditionalPropertiesIgnored())
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/pets")
                        .withBody("{\n" +
                                "  \"name\": \"Fido\",\n" +
                                "  \"petType\": \"Dog\",\n" +
                                "  \"packSize\": 3\n" +
                                "}")
                        .withContentType("application/json")
                        .build());

        check(report);
    }
}
