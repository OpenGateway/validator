package com.opengateway.validator;

import com.atlassian.oai.validator.report.ValidationReport;

import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class TestUtils {
    private TestUtils() {
    }

    static void assertValid(ValidationReport report) {
        String errors = report.getMessages().stream().map(Objects::toString).collect(joining("\n"));
        assertFalse("Found unexpected errors.\n" + errors, report.hasErrors());
    }

    static void assertInvalid(ValidationReport report) {
        assertTrue("Errors were expected, but did not find any.", report.hasErrors());
    }
}
