package com.github.automatedowl.tools;

import org.testng.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * TestNG listener for DisabledTestsCollector annotation.
 *
 * @see DisabledTestsCollector
 */
public class DisabledTestsListener implements IInvokedMethodListener {

    private Logger logger = Logger.getGlobal();
    private final Pattern TESTS_REGEX_EXPRESSION = Pattern.compile(
            Pattern.quote("@Test")
                    + "(.*?)"
                    + "(void[^\\n]*)",
            Pattern.DOTALL);
    private final Pattern DISABLED_TEST_REGEX_EXPRESSION = Pattern.compile(
            Pattern.quote("void")
                    + "(.*)",
            Pattern.DOTALL);
    private final String DISABLED_TESTS_MESSAGE =
            "You have $X disabled TestNG tests in your project.";
    private final String DISABLED_TEST_DETAILS =
            "$X is a TestNG test which currently disabled.";
    private final String SEPARATOR_STRING =
            "---------------------------------------------";

    @Override
    public void beforeInvocation(
            IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        // Do nothing.
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        AtomicReference<String> javaTestsCode = new AtomicReference<String>();

        // Initialize tests code with empty String.
        javaTestsCode.set("");

        // Check if test method had invoked.
        if (iInvokedMethod.getTestMethod().isTest() && isTestAnnotated(iInvokedMethod)) {

            // Extract java code as string from java files.
            javaTestsCode = extractJavaTestsCode(javaTestsCode, iInvokedMethod);

            // Extract disabled tests from from java tests code.
            ArrayList<String> extractedDisabledTests = extractDisabledTests(javaTestsCode.get());
            if (!extractedDisabledTests.isEmpty()) {
                logger.info(DISABLED_TESTS_MESSAGE
                        .replace("$X", Integer.toString(extractedDisabledTests.size())));
                logger.info(SEPARATOR_STRING);
                extractedDisabledTests.forEach(test -> {
                    logger.info(DISABLED_TEST_DETAILS.replace("$X", test));
                    logger.info(SEPARATOR_STRING);
                });
            } else {
                logger.info("No disabled TestNG tests have been found your project :)");
            }
        }
    }

    private AtomicReference<String> extractJavaTestsCode(
            AtomicReference<String> javaTestsCode, IInvokedMethod iInvokedMethod) {

            // Search for files of java tests classes.
            try (Stream<Path> paths = Files.walk(Paths.get(
                    System.getProperty("user.dir") + getTestsPath(iInvokedMethod)))) {
                paths.filter(Files::isRegularFile)

                        // For each java test file, concat its string.
                        .forEach(file -> {
                            String javaTestFileCode = readLinesFromFile(file.toString());
                            javaTestsCode.set(
                                    String.join("\n", javaTestsCode.get(), javaTestFileCode));
                        });
            } catch (Exception exception) {
                // Do nothing.
            }
        return javaTestsCode;
    }

    private ArrayList<String> extractDisabledTests(String testsCode) {
        Matcher matcher = TESTS_REGEX_EXPRESSION.matcher(
                testsCode);
        ArrayList<String> extractedTests = new ArrayList<>();

        // Search for test matches in java code.
        while (matcher.find()) {
            if (matcher.group().contains("enabled = false") | matcher.group().contains("enabled=false")) {
                Matcher disabledTestMatcher = DISABLED_TEST_REGEX_EXPRESSION.matcher(matcher.group());
                disabledTestMatcher.find();

                // Add extracted test and remove all its strings but test name.
                extractedTests.add(disabledTestMatcher.group()
                        .replace("void", "")
                        .replace("{", "")
                        .replace("()",""));
            }
        }
        return extractedTests;
    }

    private boolean isTestAnnotated(IInvokedMethod method) {
        return method
                .getTestMethod()
                .getConstructorOrMethod()
                .getMethod()
                .getAnnotation(DisabledTestsCollector.class) != null;
    }

    private String getTestsPath(IInvokedMethod method) {
        return method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(
                DisabledTestsCollector.class).testsPath();
    }

    private String readLinesFromFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(
                Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
