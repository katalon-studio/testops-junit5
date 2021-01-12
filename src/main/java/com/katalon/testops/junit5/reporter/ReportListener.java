package com.katalon.testops.junit5.reporter;

import com.katalon.testops.junit5.helper.LogHelper;
import com.katalon.testops.junit5.helper.TestRunManager;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;

public class ReportListener implements TestExecutionListener {

    private static final Logger logger = LogHelper.getLogger();

    TestRunManager testRunManager;

    public ReportListener() {
        super();
        testRunManager = new TestRunManager();
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        tryCatch(() -> {
            testRunManager.testPlanExecutionStarted(testPlan);
        });
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        tryCatch(() -> {
            testRunManager.testPlanExecutionFinished(testPlan);
        });
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        tryCatch(() -> {
            testRunManager.dynamicTestRegistered(testIdentifier);
        });
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        tryCatch(() -> {
            testRunManager.executionSkipped(testIdentifier, reason);
        });
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        tryCatch(() -> {
            testRunManager.executionStarted(testIdentifier);
        });
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        tryCatch(() -> {
            testRunManager.executionFinished(testIdentifier, testExecutionResult);
        });
    }

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        tryCatch(() -> {
            testRunManager.reportingEntryPublished(testIdentifier, entry);
        });
    }

    private void tryCatch(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            logger.error("An error has occurred in TestOps Reporter", e);
        }
    }

}
