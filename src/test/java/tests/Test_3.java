
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
public class Test_3 extends BaseTest {
    @DataProvider(name = "signInData")
    public Object[][] getSignInData() {
        List<String[]> csvData = CsvUtils.readResourceCsv("animal.csv", true);
        Object[][] testData = new Object[csvData.size()][];
        for (int i = 0; i < csvData.size(); i++) {
            testData[i] = csvData.get(i);
        }
        return testData;
    }

    @Test(dataProvider = "signInData")
    public void testProductSearch(String searchTerm) {
        System.out.println("🔍 Searching for: " + searchTerm);


        // Locate and interact with the search input
        HomePage homePage = new HomePage(driver);
        homePage.searchFor(searchTerm);

        // Optional: Add validation or result check here
        System.out.println("✅ Search triggered for: " + searchTerm);
    }
}


