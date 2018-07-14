# testng-disabled-tests

Java library which allows to collect disabled TestNG tests in given TestNG project, using simple annotation.

### Example of common usage:

```
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
```

### Received output

```
Jul 14, 2018 11:57:28 AM com.github.automatedowl.tools.DisabledTestsListener afterInvocation
INFO: You have 2 disabled TestNG tests in your project.
Jul 14, 2018 11:57:28 AM com.github.automatedowl.tools.DisabledTestsListener afterInvocation
INFO: ---------------------------------------------
Jul 14, 2018 11:57:28 AM com.github.automatedowl.tools.DisabledTestsListener lambda$afterInvocation$0
INFO:  firstDisabledTest is a TestNG test which currently disabled.
Jul 14, 2018 11:57:28 AM com.github.automatedowl.tools.DisabledTestsListener lambda$afterInvocation$0
INFO: ---------------------------------------------
Jul 14, 2018 11:57:28 AM com.github.automatedowl.tools.DisabledTestsListener lambda$afterInvocation$0
INFO:  secondDisabledTest is a TestNG test which currently disabled.
Jul 14, 2018 11:57:28 AM com.github.automatedowl.tools.DisabledTestsListener lambda$afterInvocation$0
INFO: ---------------------------------------------

```
### Annotation values

DisabledTestsCollector annotation has just one String value, 'testsPath'.
You would have to locate your tests classes and define it as 'testPath' value,
in order to direct the collector to the java test classes files. 
The trivial path in java project would be '/src/test/java', but you may adjust it for your needs. 
You would not have to write any code in the test itself. The listener would parse you tests path and would the job of collecting your disabled tests.  

```
@DisabledTestsCollector(testsPath = "/src/test/java")
```

### Maven dependencies

TestNG:
```
    <dependency>
        <groupId>com.github.automatedowl</groupId>
        <artifactId>testng-disabled-tests</artifactId>
        <version>1.0.0</version>
    </dependency>
```
