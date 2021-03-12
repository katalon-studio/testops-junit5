package com.katalon.testops.junit5.helper;

import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.Metadata;
import com.katalon.testops.commons.model.Status;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.junit5.reporter.ReportListener;
import com.katalon.testops.junit5.reporter.TestCaseExecution;
import org.junit.platform.engine.TestExecutionResult;

import static com.katalon.testops.commons.helper.StringHelper.getErrorMessage;
import static com.katalon.testops.commons.helper.StringHelper.getStackTraceAsString;

public final class ReportHelper {

    public static Metadata createMetadata() {
        Metadata metadata = new Metadata();
        metadata.setFramework("junit5");
        metadata.setLanguage("java");
        metadata.setVersion(ReportListener.class.getPackage().getImplementationVersion());
        return metadata;
    }

    public static TestResult createTestResult(TestCaseExecution testCaseExecution) {
        String uuid = GeneratorHelper.generateUniqueValue();
        TestExecutionResult testExecutionResult = testCaseExecution.getResult();

        TestResult testResult = new TestResult();
        testResult.setUuid(uuid);
        if (testCaseExecution.isSkipped()) {
            testResult.setStatus(Status.SKIPPED);
        } else {
            testResult.setStatus(getStatus(testExecutionResult));
        }
        testResult.setName(testCaseExecution.getTestCaseName());
        testResult.setSuiteName(testCaseExecution.getTestSuite().getTestSuiteName());
        testResult.setParentUuid(testCaseExecution.getTestSuite().getUuid());

        if (testResult.getStatus() != Status.PASSED) {
            if (testResult.getStatus() == Status.SKIPPED) {
                testResult.setErrorMessage(testCaseExecution.getSkipMessage());
            } else {
                testExecutionResult.getThrowable().ifPresent((throwable -> {
                    testResult.setErrorMessage(getErrorMessage(throwable));
                    testResult.setStackTrace(getStackTraceAsString(throwable));
                }));
            }
        }

        return testResult;
    }

    private static Status getStatus(TestExecutionResult testExecutionResult) {
        switch (testExecutionResult.getStatus()) {
            case FAILED:
            case ABORTED:
                return Status.FAILED;
            case SUCCESSFUL: return Status.PASSED;
        }
        return Status.INCOMPLETE;
    }

}