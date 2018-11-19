package com.opengateway.validator;



import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.ValidationReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.opengateway.validator.TestUtils.check;
import static java.lang.String.join;
import static java.util.Collections.EMPTY_LIST;
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

        check(report);
    }

    @Test
    public void simpleRouteValidation() {

        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor("/simple_route.yaml")
                .build();

        final ValidationReport report = validator.validateRequest(
                new SimpleRequest.Builder(Request.Method.POST, "/simpleRoute")
                        .withBody("{\"simpleString\": \"kdjsh\", \"simpleInt\": 123}")
                        .withContentType("application/json")
                        .build());

        check(report);
    }
}

