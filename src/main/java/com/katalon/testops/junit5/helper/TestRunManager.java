package com.katalon.testops.junit5.helper;

import com.katalon.testops.commons.ReportLifecycle;
import com.katalon.testops.commons.helper.GeneratorHelper;
import com.katalon.testops.commons.model.TestResult;
import com.katalon.testops.commons.model.TestSuite;
import com.katalon.testops.junit5.reporter.TestCaseExecution;
import com.katalon.testops.junit5.reporter.TestSuiteExecution;
import org.apache.commons.lang3.StringUtils;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class TestRunManager {

    private static final Pattern COMMA_PATTERN = Pattern.compile( "," );

    private static final Logger logger = LogHelper.getLogger();

    private ReportLifecycle reportLifecycle;
    private ConcurrentMap<String, TestSuiteExecution> testSuites;
    private TestPlan testPlan;

    public TestRunManager() {
        reportLifecycle = new ReportLifecycle();
        testSuites = new ConcurrentHashMap<>();
    }

    public void testPlanExecutionStarted(TestPlan testPlan) {
        logger.info("testPlanExecutionStarted");
        this.testPlan = testPlan;
        reportLifecycle.startExecution();
        reportLifecycle.writeMetadata(ReportHelper.createMetadata());
    }

    public void testPlanExecutionFinished(TestPlan testPlan) {
        logger.info("testPlanExecutionFinished");
        reportLifecycle.stopExecution();
        reportLifecycle.writeTestResultsReport();
        reportLifecycle.writeTestSuitesReport();
        reportLifecycle.writeExecutionReport();
        reportLifecycle.upload();
        reportLifecycle.reset();
    }

    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        logger.info("dynamicTestRegistered: " + testIdentifier.getDisplayName());
    }

    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        if (testIdentifier.isTest()) {
            skipTestCase(testIdentifier, reason);
            return;
        }
        skipTestSuite(testIdentifier, reason);
    }

    public void executionStarted(TestIdentifier testIdentifier) {
        if (isJupiterExecution(testIdentifier)) {
            return;
        }
        if (testIdentifier.isTest()) {
            startTestCase(testIdentifier);
            return;
        }
        startTestSuite(testIdentifier);
    }

    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (isJupiterExecution(testIdentifier)) {
            return;
        }
        if (testIdentifier.isTest()) {
            endTestCase(testIdentifier, testExecutionResult);
            return;
        }
        endTestSuite(testIdentifier, testExecutionResult);
    }

    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        logger.info("reportingEntryPublished: " + testIdentifier.getDisplayName());
    }

    private void startTestSuite(TestIdentifier testIdentifier) {
        logger.info("startTestSuite: " + testIdentifier.getLegacyReportingName());
        String uuid = GeneratorHelper.generateUniqueValue();
        TestSuiteExecution testSuiteExecution = new TestSuiteExecution(testIdentifier, uuid);
        testSuiteExecution.setMethodNames(toClassMethodName(testIdentifier));
        TestSuite testSuite = new TestSuite();
        testSuite.setName(testIdentifier.getLegacyReportingName());
        reportLifecycle.startSuite(testSuite, uuid);
        testSuites.putIfAbsent(testIdentifier.getUniqueId(), testSuiteExecution);
    }

    private void startTestCase(TestIdentifier testIdentifier) {
        logger.info("startTestCase: " + testIdentifier.getDisplayName());
        reportLifecycle.startTestCase();
    }


    private void endTestSuite(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        logger.info("endTestSuite: " + testIdentifier.getLegacyReportingName());
        TestSuiteExecution testSuiteExecution = testSuites.remove(testIdentifier.getUniqueId());
        if (Objects.isNull(testSuiteExecution)) {
            return;
        }
        reportLifecycle.stopTestSuite(testSuiteExecution.getUuid());
    }

    private void endTestCase(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        logger.info("endTestCase: " + testIdentifier.getDisplayName());
        TestSuiteExecution testSuiteExecution = testSuites.get(testIdentifier.getParentId().get());
        TestCaseExecution testCaseExecution = new TestCaseExecution(testIdentifier, testSuiteExecution);
        testCaseExecution.setMethodNames(toClassMethodName(testIdentifier));
        testCaseExecution.setResult(testExecutionResult);
        TestResult testResult = ReportHelper.createTestResult(testCaseExecution);
        reportLifecycle.stopTestCase(testResult);
    }

    private void skipTestCase(TestIdentifier testIdentifier, String reason) {
        logger.info("skipTestCase: " + testIdentifier.getDisplayName());
        TestCaseExecution testCaseExecution = new TestCaseExecution(testIdentifier, testSuites.get(testIdentifier.getParentId().get()));
        testCaseExecution.setSkipped(true);
        testCaseExecution.setSkipMessage(reason);
        TestResult testResult = ReportHelper.createTestResult(testCaseExecution);
        reportLifecycle.stopTestCase(testResult);
    }

    private void skipTestSuite(TestIdentifier testIdentifier, String reason) {
        logger.info("skipTestSuite: " + testIdentifier.getDisplayName());
    }

    private boolean isJupiterExecution(TestIdentifier testIdentifier) {
        return testIdentifier.isContainer() && "JUnit Jupiter".equals(testIdentifier.getLegacyReportingName());
    }

    /**
     * <ul>
     *     <li>[0] class name - used in stacktrace parser</li>
     *     <li>[1] class display name</li>
     *     <li>[2] method signature - used in stacktrace parser</li>
     *     <li>[3] method display name</li>
     * </ul>
     *
     * @param testIdentifier a class or method
     * @return 4 elements string array
     */
    private String[] toClassMethodName(TestIdentifier testIdentifier) {
        Optional<TestSource> testSource = testIdentifier.getSource();
        String display = testIdentifier.getDisplayName();

        if (testSource.filter(MethodSource.class::isInstance).isPresent()) {
            MethodSource methodSource = testSource.map(MethodSource.class::cast).get();
            String realClassName = methodSource.getClassName();

            String[] source = testPlan.getParent(testIdentifier)
                    .map(this::toClassMethodName)
                    .map(s -> new String[]{s[0], s[1]})
                    .orElse(new String[]{realClassName, realClassName});

            String simpleClassNames = COMMA_PATTERN.splitAsStream(methodSource.getMethodParameterTypes())
                    .map(s -> s.substring(1 + s.lastIndexOf('.')))
                    .collect(joining(","));

            boolean hasParams = StringUtils.isNotBlank(methodSource.getMethodParameterTypes());
            String methodName = methodSource.getMethodName();
            String description = testIdentifier.getLegacyReportingName();
            String methodSign = hasParams ? methodName + '(' + simpleClassNames + ')' : methodName;
            boolean equalDescriptions = display.equals(description);
            boolean hasLegacyDescription = description.startsWith(methodName + '(');
            boolean hasDisplayName = !equalDescriptions || !hasLegacyDescription;
            String methodDesc = equalDescriptions || !hasParams ? methodSign : description;
            String methodDisp = hasDisplayName ? display : methodDesc;

            // The behavior of methods getLegacyReportingName() and getDisplayName().
            //     test      ||  legacy  |  display
            // ==============||==========|==========
            //    normal     ||    m()   |    m()
            //  normal+displ ||   displ  |  displ
            // parameterized ||  m()[1]  |  displ

            return new String[]{source[0], source[1], methodDesc, methodDisp};
        } else if (testSource.filter(ClassSource.class::isInstance).isPresent()) {
            ClassSource classSource = testSource.map(ClassSource.class::cast).get();
            String className = classSource.getClassName();
            String simpleClassName = className.substring(1 + className.lastIndexOf('.'));
            String source = display.equals(simpleClassName) ? className : display;
            return new String[]{className, source, null, null};
        } else {
            String source = testPlan.getParent(testIdentifier)
                    .map(TestIdentifier::getDisplayName).orElse(display);
            return new String[]{source, source, display, display};
        }
    }

}
