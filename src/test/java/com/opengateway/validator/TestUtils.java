package com.opengateway.validator;

import com.atlassian.oai.validator.report.ValidationReport;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static org.junit.Assert.assertFalse;

class TestUtils {
    private TestUtils() {
    }

    static void check(ValidationReport report) {
        String errors = join("\n",
                report.getMessages().stream().map(Objects::toString).collect(Collectors.toList()));
        assertFalse("Found unexpected errors.\n" + errors, report.hasErrors());

    }
}
