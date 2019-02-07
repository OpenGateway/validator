package com.opengateway.validator;



import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.opengateway.validator.TestUtils.assertValid;
import static java.lang.String.join;
import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
public class SimpleValidationTest {


    @Test
    public void simpleValidation() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/contract.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/pets")
                        .withBody("{\"name\": \"kk\"}")
                        .withContentType("application/json")
                        .build());

        assertValid(report);
    }

    @Test
    public void simpleRouteValidation() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/simple_route_1.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/simpleRoute")
                        .withBody("{\"simpleString\": \"kdjsh\", \"simpleInt\": 123}")
                        .withContentType("application/json")
                        .build());

        assertValid(report);
    }
}

