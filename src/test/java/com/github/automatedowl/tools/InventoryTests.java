package com.github.automatedowl.tools;

import org.testng.Assert;
import org.testng.annotations.*;

/** Class that contains TestNG unit tests for collecting disabled tests. */
@Listeners(DisabledTestsListener.class)
public class InventoryTests {

    @Test
    @DisabledTestsCollector(testsPath = "/src/test/java")
    void getDisabledTest() {
        // This test would collect all disabled tests in TestNG project.
    }

    @Test
    void enabledTest() {
        Assert.assertTrue(true);
    }

    @Test(enabled = false)
    void firstDisabledTest() {
        Assert.assertTrue(true);
    }

    @Test(enabled = false)
    void secondDisabledTest() {
        Assert.assertTrue(true);
    }
}
