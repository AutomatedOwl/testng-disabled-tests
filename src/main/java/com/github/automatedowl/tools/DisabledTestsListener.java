package com.github.automatedowl.tools;

import org.testng.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        // Do nothing.
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        AtomicReference<String> javaTestsCode = new AtomicReference<String>();

        // Initialize tests code with empty String.
        javaTestsCode.set("");

        // Check if test method had invoked.
        if (iInvokedMethod.getTestMethod().isTest() && isTestAnnotated(iInvokedMethod)) {

            // Search for files of java tests classes.
            try (Stream<Path> paths = Files.walk(Paths.get(
                    System.getProperty("user.dir") + getTestsPath(iInvokedMethod)))) {
                paths.filter(Files::isRegularFile)

                        // For each java test file, concat its string. 
                        .forEach(file -> {
                            String javaTestFileCode = readLinesFromFile(file.toString());
                            javaTestsCode.set(String.join("\n", javaTestsCode.get(), javaTestFileCode));
                        });
            } catch (Exception exception) {
                // Do nothing.
            }
            
            String extractedTests = extractTests(javaTestsCode.get());
            logger.info(extractedTests);
            String disabledTests = getDisabledTests(extractedTests);
            if (disabledTests != null) {
                logger.info(disabledTests);
            }
        }

       // logger.info(javaTestsCode.get());
    }

    private String getDisabledTests(String extractedTests) {
        return null;
    }

    private String extractTests(String testsCode) {

        Matcher matcher = TESTS_REGEX_EXPRESSION.matcher(
                testsCode);
        String extractedTests = "";

        // Index for regex matches.
        int regexMatchIndex = 1;
        while (matcher.find()) {
            //logger.info("Result index: " + regexMatchIndex);
            //logger.info(matcher.group());
            if (matcher.group().contains("enabled = false") | matcher.group().contains("enabled=false")) {
                Matcher disabledTestMatcher = DISABLED_TEST_REGEX_EXPRESSION.matcher(matcher.group());
                disabledTestMatcher.find();
                extractedTests = String.join("\n", extractedTests,
                        disabledTestMatcher.group()
                                .replace("void", "")
                                .replace("{", ""));
            }
            regexMatchIndex++;
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
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
