package com.opengateway.validator;

import com.atlassian.oai.validator.report.ValidationReport;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class NullValidationReport implements ValidationReport {

    NullValidationReport() {
    }

    @Override
    public boolean hasErrors() {
        return true;
    }

    @Nonnull
    @Override
    public List<Message> getMessages() {
        return Collections.emptyList();
    }

    @Override
    public ValidationReport withAdditionalContext(final MessageContext context) {
        return this;
    }
}
