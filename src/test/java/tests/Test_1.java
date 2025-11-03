package tests;

import base.BaseTest;
import pages.HomePage;
import utils.CsvUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class Test_1 extends BaseTest {

    @DataProvider(name = "signUpData")
    public Object[][] getSignUpData() {
        List<String[]> csvData = CsvUtils.readResourceCsv("signup.csv", true);
        Object[][] testData = new Object[csvData.size()][];
        for (int i = 0; i < csvData.size(); i++) {
            testData[i] = csvData.get(i);
        }
        return testData;
    }

    @Test(dataProvider = "signUpData")
    public void testSignUp(String username, String password, String repeatedPassword, String firstName,
                           String lastName, String email, String phone, String address1,
                           String address2, String city, String state, String zip,
                           String country, String testType, String expectedResult) {

        // Initialize HomePage or use direct locator
        // Option 1: Use HomePage (preferred if implementation is reliable)
        HomePage homePage = new HomePage(driver);
        homePage.clickSignUp();

        // Option 2: Revert to original locator if HomePage.clickSignUp() is unreliable
        // WebElement signUpLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Sign Up']")));
        // signUpLink.click();

        // Fill username
        WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("username")));
        usernameField.sendKeys(username);

        // Fill password
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));
        passwordField.sendKeys(password);

        // Scroll to country field
        WebElement countryField = driver.findElement(By.name("country"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", countryField);

        // Fill repeated password
        WebElement repeatedPasswordField = wait.until(ExpectedConditions.elementToBeClickable(By.name("repeatedPassword")));
        repeatedPasswordField.sendKeys(repeatedPassword);

        // Fill first name
        WebElement firstNameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("firstName")));
        firstNameField.sendKeys(firstName);

        // Fill last name
        WebElement lastNameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("lastName")));
        lastNameField.sendKeys(lastName);

        // Fill email
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
        emailField.sendKeys(email);

        // Fill phone
        WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(By.name("phone")));
        phoneField.sendKeys(phone);

        // Fill address1
        WebElement address1Field = wait.until(ExpectedConditions.elementToBeClickable(By.name("address1")));
        address1Field.sendKeys(address1);

        // Fill address2
        WebElement address2Field = wait.until(ExpectedConditions.elementToBeClickable(By.name("address2")));
        address2Field.sendKeys(address2);

        // Fill city
        WebElement cityField = wait.until(ExpectedConditions.elementToBeClickable(By.name("city")));
        cityField.sendKeys(city);

        // Fill state
        WebElement stateField = wait.until(ExpectedConditions.elementToBeClickable(By.name("state")));
        stateField.sendKeys(state);

        // Fill zip
        WebElement zipField = wait.until(ExpectedConditions.elementToBeClickable(By.name("zip")));
        zipField.sendKeys(zip);

        // Fill country
        countryField.sendKeys(country);

        // Scroll to language preference
        WebElement languagePreference = driver.findElement(By.name("languagePreference"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", languagePreference);

        // Select language preference
        Select languageSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("languagePreference"))));
        languageSelect.selectByValue("english");

        // Select favourite category
        Select categorySelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("favouriteCategoryId"))));
        categorySelect.selectByValue("DOGS");

        // Click list option
        WebElement listOption = wait.until(ExpectedConditions.elementToBeClickable(By.name("listOption")));
        listOption.click();

        // Click banner option
        WebElement bannerOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='true' and @name='bannerOption']")));
        bannerOption.click();

        // Submit form (fixed locator)
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitButton.click();
    }
}