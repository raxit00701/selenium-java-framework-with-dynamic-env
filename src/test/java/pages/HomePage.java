package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends BasePage {

    @FindBy(xpath = "//a[normalize-space()='Sign In']")
    private WebElement signInLink;

    @FindBy(xpath = "//a[normalize-space()='Sign Up']")
    private WebElement signUpLink;

    @FindBy(css = "input[placeholder='Product Search']")
    private WebElement searchInput;

    @FindBy(xpath = "//button[normalize-space()='Search']")
    private WebElement searchBtn;

    // 👇 Clinic promo link (yeh hi missing tha)
    @FindBy(css = "h4 a[title='Visit a pet clinic with excellent non-profit veterinarians.']")
    private WebElement clinicLink;

    // Optional: search/catalog/result container to verify results visible
    @FindBy(css = ".search-results, .catalog, .content")
    private WebElement resultsContainer;

    public HomePage(WebDriver driver) {
        super(driver);               // BasePage will do PageFactory.initElements(...)
        PageFactory.initElements(driver, this); // safe to keep even if BasePage already does it
    }

    public void clickSignIn() {
        click(signInLink);
    }

    public void clickSignUp() {
        click(signUpLink);
    }

    public void searchFor(String term) {
        type(searchInput, term);
        click(searchBtn);
    }

    // 👇 The missing method
    public void openClinicPromo() {
        click(clinicLink);
    }

    public boolean isSearchResultsShown() {
        return isDisplayed(resultsContainer);
    }
}
