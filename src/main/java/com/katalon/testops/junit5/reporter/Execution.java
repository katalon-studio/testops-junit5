package com.katalon.testops.junit5.reporter;

import org.junit.platform.launcher.TestIdentifier;

abstract class Execution {
    protected TestIdentifier testIdentifier;
    protected String[] methodNames;

    protected Execution(TestIdentifier testIdentifier) {
        this.testIdentifier = testIdentifier;
    }

    public TestIdentifier getTestIdentifier() {
        return this.testIdentifier;
    }

    public void setTestIdentifier(TestIdentifier testIdentifier) {
        this.testIdentifier = testIdentifier;
    }

    public void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
    }
}
