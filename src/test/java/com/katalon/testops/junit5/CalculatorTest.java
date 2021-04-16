package com.katalon.testops.junit5;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private static Calculator calculator;

    @BeforeAll
    static void setUp() {
        calculator = new Calculator();
    }

    @Test
    void add() {
        assertEquals(377, calculator.add(365, 12));
        assertEquals(377, calculator.add(12, 365));
    }

    @Test
    void subtract() {
        assertEquals(353, calculator.subtract(365, 12));
        assertEquals(-353, calculator.subtract(12, 365));
    }

    @Test
    void multiply() {
        assertEquals(4380, calculator.multiply(365, 12));
        assertEquals(4380, calculator.multiply(12, 365));
    }

    @Test
    void divide() {
        assertEquals(36.5, calculator.divide(365, 10));
    }

    @Test
    void divideByZeroPassed() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(365, 0);
        });

        assertTrue(exception.getMessage().contains("zero"));
    }

    @Test
    void divideByZeroFailed() {
        assertThrows(IllegalStateException.class, () -> {
            calculator.divide(365, 0);
        });
    }

    @Test
    void divideByZeroError() {
        calculator.divide(365, 0);
    }

    @Disabled("this test is disabled")
    @Test
    void divideByZeroSkipped() {
        calculator.divide(365, 0);
    }

    @Test
    void divideByZeroSkippedByAssumption() {
        Assumptions.assumeTrue(false);
        calculator.divide(365, 0);
    }


    @Timeout(1)
    @Test
    void divideByZeroTimeoutError() throws InterruptedException {
        Thread.sleep(3000);
    }
}
