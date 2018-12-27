package com.opengateway.validator;


import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.LevelResolverFactory;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.opengateway.validator.TestUtils.assertInvalid;
import static com.opengateway.validator.TestUtils.assertValid;

@RunWith(SpringRunner.class)
public class PolymorphismTest {
    private static final String CONTRACT_LOCATION = "/petstore-workaround.yaml";
    private OpenApiInteractionValidator validator;

    @Before
    public void setUp () {
        validator = OpenApiInteractionValidator
                .createFor(CONTRACT_LOCATION)
                .withLevelResolver(LevelResolverFactory.withAdditionalPropertiesIgnored())
                .build();
    }

    @Test
    public void validatesObject() {
        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/pets")
                        .withBody("{\n" +
                                "  \"name\": \"Fido\",\n" +
                                "  \"petType\": \"Dog\",\n" +
                                "  \"packSize\": 3\n" +
                                "}")
                        .withContentType("application/json")
                        .build());

        assertValid(report);
    }

    @Test
    public void invalidatesObjectWithWrongDiscriminator() {
        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/pets")
                        .withBody("{\n" +
                                "  \"name\": \"Fido\",\n" +
                                "  \"petType\": \"Cat\",\n" +
                                "  \"packSize\": 3\n" +
                                "}")
                        .withContentType("application/json")
                        .build());

        assertInvalid(report);
    }

    @Test
    public void validatesArrayOfObjects() {
        final ValidationReport report = validator.validate(
                new SimpleRequest.Builder(Request.Method.GET, "/pets")
                        .withContentType("application/json")
                        .build(),
                new SimpleResponse.Builder(200)
                        .withBody("[{\n" +
                                "  \"name\": \"Fido\",\n" +
                                "  \"petType\": \"Dog\",\n" +
                                "  \"packSize\": 3\n" +
                                "}, \n" +
                                "{\n" +
                                "  \"name\": \"Garfield\",\n" +
                                "  \"petType\": \"Cat\",\n" +
                                "  \"huntingSkill\": \"lazy\"\n" +
                                "}]"
                        )
                        .withContentType("application/json")
                        .build());

        assertValid(report);
    }
}
