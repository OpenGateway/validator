package com.opengateway.validator;

import com.atlassian.oai.validator.report.ValidationReport;

public class ValidationReportHolder {

    private ValidationReport report;

    public void add(ValidationReport report) {
        if (report == null) {
            this.report = new NullValidationReport();
        } else {
            this.report = report;
        }
    }

    public ValidationReport get() {
        return report;
    }
}
