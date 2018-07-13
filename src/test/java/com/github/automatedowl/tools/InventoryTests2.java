package com.github.automatedowl.tools;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** Class that contains TestNG unit tests for Chromedriver JS errors collector. */
@Listeners(DisabledTestsListener.class)
public class InventoryTests2 {

    @Test
    @DisabledTestsCollector(testsPath = "/src/test/java")
    void getDisabledTest() {

    }

    @Test
    void enabledTest() {
        Assert.assertTrue(true);
    }

    @Test(enabled = false)
    void disabledTest() {
        Assert.assertTrue(true);
    }
}
