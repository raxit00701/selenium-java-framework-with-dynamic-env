package tests;

import base.BaseTest;
import utils.CsvUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

public class Test_4 extends BaseTest {

    @DataProvider(name = "signInData")
    public Object[][] getSignInData() {
        List<String[]> csvData = CsvUtils.readResourceCsv("payment.csv", true);
        Object[][] testData = new Object[csvData.size()][];
        for (int i = 0; i < csvData.size(); i++) {
            testData[i] = csvData.get(i);
        }
        return testData;
    }

    @Test(dataProvider = "signInData")
    public void testCheckoutFlow(String cardNumber, String expiry, String firstName, String lastName,
                                 String address1, String address2, String city, String state,
                                 String zip, String country) {
        // Log input data
        logData(cardNumber, expiry, firstName, lastName, address1, address2, city, state, zip, country);

        // Consolidated navigation and cart actions
        performNavigationAndAddToCart();

        // Update quantity
        interactWithElement(By.xpath("//input[@name='EST-6']"), element -> {
            element.clear();
            element.sendKeys("10");
        });

        // Proceed to checkout
        scrollAndClick(By.linkText("Proceed to Checkout"));

        // Fill payment and billing info
        fillPaymentForm(cardNumber, expiry, firstName, lastName, address1, address2, city, state, zip, country);

        // Handle validation and confirmation
        handleCheckoutSubmission();
    }

    private void logData(String... data) {
        String[] labels = {"cardNumber", "expiry", "firstName", "lastName", "address1", "address2", "city", "state", "zip", "country"};
        StringBuilder log = new StringBuilder("Running test with data: ");
        for (int i = 0; i < data.length; i++) {
            log.append(labels[i]).append("=").append(data[i]).append(", ");
        }
        System.out.println(log.substring(0, log.length() - 2));
    }

    private void performNavigationAndAddToCart() {
        String[][] actions = {
                {"a[href='/account/signonForm']", "css"},
                {"div.button-bar button[type='submit']", "css"},
                {"//div[@id='QuickLinks']//a[normalize-space()='Dogs']", "xpath"},
                {"a[href='/products/K9-BD-01']", "css"},
                {"//a[normalize-space()='EST-6']", "xpath"},
                {"//a[normalize-space()='Add to Cart']", "xpath"}
        };

        for (String[] action : actions) {
            By locator = action[1].equals("css") ? By.cssSelector(action[0]) : By.xpath(action[0]);
            scrollAndClick(locator);
        }
    }

    private void fillPaymentForm(String cardNumber, String expiry, String firstName, String lastName,
                                 String address1, String address2, String city, String state,
                                 String zip, String country) {
        selectOption(By.name("cardType"), "MasterCard");
        interactWithElement(By.name("creditCard"), element -> {
            element.clear();
            element.sendKeys(cardNumber);
        });
        String[][] fields = {
                {"expiryDate", expiry},
                {"billToFirstName", firstName},
                {"billToLastName", lastName},
                {"billAddress1", address1},
                {"billAddress2", address2},
                {"billCity", city},
                {"billState", state},
                {"billZip", zip},
                {"billCountry", country}
        };

        for (String[] field : fields) {
            interactWithElement(By.name(field[0]), element -> {
                element.clear();
                element.sendKeys(field[1]);
            });
        }
    }

    private void handleCheckoutSubmission() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Step 0️⃣: Click Continue
        By continueButton = By.xpath("//button[normalize-space()='Continue']");
        scrollAndClick(continueButton);

        try {
            // Step 1️⃣: Check for validation errors
            List<WebElement> errors = driver.findElements(
                    By.xpath("//span[normalize-space()='must not be blank' or normalize-space()='must be numeric characters']")
            );

            if (!errors.isEmpty()) {
                System.out.println("⚠️ Validation error present, closing test case.");
                return; // stop execution if validation messages exist
            }

            // Step 2️⃣: Scroll & click submit button inside button-bar
            By submitButton = By.cssSelector("div.button-bar button[type='submit']");
            shortWait.until(ExpectedConditions.presenceOfElementLocated(submitButton));
            scrollAndClick(submitButton);

            // Step 3️⃣: Scroll & click final button[type='button']
            By finalButton = By.cssSelector("button[type='button']");
            shortWait.until(ExpectedConditions.presenceOfElementLocated(finalButton));
            scrollAndClick(finalButton);

        } catch (TimeoutException e) {
            System.out.println("⚠️ Expected element not found: " + e.getMessage());
        }
    }

    /**
     * Scrolls the element into view and clicks.
     */
    private void scrollAndClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
    }

    /**
     * Generic element interaction with consumer.
     */
    private void interactWithElement(By locator, Consumer<WebElement> action) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        action.accept(element);
    }

    /**
     * Select dropdown option by visible text.
     */
    private void selectOption(By locator, String value) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        Select select = new Select(dropdown);
        select.selectByVisibleText(value);
    }
}