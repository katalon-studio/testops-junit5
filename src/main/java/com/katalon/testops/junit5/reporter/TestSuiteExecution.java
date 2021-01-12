package com.katalon.testops.junit5.reporter;

import org.junit.platform.launcher.TestIdentifier;

public class TestSuiteExecution extends Execution {

    private String uuid;

    public TestSuiteExecution(TestIdentifier testIdentifier, String uuid) {
        super(testIdentifier);
        this.uuid = uuid;
    }

    public String getTestSuiteName() {
        return this.methodNames[0];
    }

    public String getUuid() {
        return this.uuid;
    }

}
