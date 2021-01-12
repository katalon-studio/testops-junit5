package com.katalon.testops.junit5.reporter;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;

public class TestCaseExecution extends Execution {

    private TestSuiteExecution testSuite;
    private String skipMessage;
    private boolean skipped;
    private TestExecutionResult result;

    public TestCaseExecution(TestIdentifier testIdentifier, TestSuiteExecution testSuite) {
        super(testIdentifier);
        this.testSuite = testSuite;
    }

    public String getTestCaseName() {
        return this.methodNames[0] + "." + this.methodNames[2];
    }

    public TestSuiteExecution getTestSuite() {
        return this.testSuite;
    }

    public void setSkipMessage(String skipMessage) {
        this.skipMessage = skipMessage;
    }

    public String getSkipMessage() {
        return this.skipMessage;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    public boolean isSkipped() {
        return this.skipped;
    }

    public void setResult(TestExecutionResult result) {
        this.result = result;
    }

    public TestExecutionResult getResult() {
        return this.result;
    }

}
