
package tests;


import base.BaseTest;
import pages.HomePage;
import utils.CsvUtils;
import base.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class Test_2 extends BaseTest {

    @DataProvider(name = "signInData")
    public Object[][] getSignInData() {
        List<String[]> csvData = CsvUtils.readResourceCsv("signin.csv", true);
        Object[][] testData = new Object[csvData.size()][];
        for (int i = 0; i < csvData.size(); i++) {
            testData[i] = csvData.get(i);
        }
        return testData;
    }

    @Test(dataProvider = "signInData")
    public void testSignIn(String username, String password, String testType, String expectedResult) {
        // Click Sign In link
        HomePage homePage = new HomePage(driver);
        homePage.clickSignIn();

        // Clear and fill username
        WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("username")));
        usernameField.clear();
        usernameField.sendKeys(username);
        System.out.println("Entered username: " + username);

        // Clear and fill password
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));
        passwordField.clear();
        passwordField.sendKeys(password);
        System.out.println("Entered password");

        // Click submit button
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitButton.click();
        System.out.println("Clicked submit button");


    }
}