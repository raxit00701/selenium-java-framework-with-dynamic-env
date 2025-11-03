package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;
    protected final long defaultTimeoutSec;

    // ✅ Constructor: PageFactory init
    public BasePage(WebDriver driver) {
        this(driver, 20);
    }

    public BasePage(WebDriver driver, long timeoutSeconds) {
        this.driver = driver;
        this.defaultTimeoutSec = timeoutSeconds;
        this.wait  = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this); // 🟢 PageFactory init
    }

    // ----------------- WAIT HELPERS -----------------
    public WebElement waitVisible(WebElement el) {
        return wait.until(ExpectedConditions.visibilityOf(el));
    }

    public WebElement waitClickable(WebElement el) {
        return wait.until(ExpectedConditions.elementToBeClickable(el));
    }

    public boolean waitInvisible(WebElement el) {
        return wait.until(ExpectedConditions.invisibilityOf(el));
    }

    public List<WebElement> waitAllVisible(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    // ----------------- ELEMENT HELPERS -----------------
    public void click(WebElement el) {
        waitClickable(el).click();
    }

    public void type(WebElement el, String text) {
        WebElement element = waitVisible(el);
        element.clear();
        element.sendKeys(text);
    }

    public void clear(WebElement el) {
        waitVisible(el).clear();
    }

    public String getText(WebElement el) {
        return waitVisible(el).getText();
    }

    public String getAttr(WebElement el, String attr) {
        return waitVisible(el).getAttribute(attr);
    }

    public String getCss(WebElement el, String prop) {
        return waitVisible(el).getCssValue(prop);
    }

    public boolean isDisplayed(WebElement el) {
        try { return waitVisible(el).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    public boolean isEnabled(WebElement el) { return waitVisible(el).isEnabled(); }
    public boolean isSelected(WebElement el) { return waitVisible(el).isSelected(); }

    // ----------------- SELECT HELPERS -----------------
    public void selectByVisibleText(WebElement el, String text) {
        new Select(waitVisible(el)).selectByVisibleText(text);
    }

    public void selectByValue(WebElement el, String value) {
        new Select(waitVisible(el)).selectByValue(value);
    }

    public void selectByIndex(WebElement el, int index) {
        new Select(waitVisible(el)).selectByIndex(index);
    }

    // ----------------- MOUSE & KEYBOARD -----------------
    public void hover(WebElement el) { actions.moveToElement(waitVisible(el)).perform(); }
    public void doubleClick(WebElement el) { actions.doubleClick(waitVisible(el)).perform(); }
    public void rightClick(WebElement el) { actions.contextClick(waitVisible(el)).perform(); }
    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(waitVisible(source), waitVisible(target)).perform();
    }

    // ----------------- JS HELPERS -----------------
    public Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public void jsClick(WebElement el) { js("arguments[0].click();", waitVisible(el)); }

    public void scrollIntoView(WebElement el, boolean alignToTop) {
        js("arguments[0].scrollIntoView(arguments[1]);", waitVisible(el), alignToTop);
    }

    // ----------------- UTILS -----------------
    public void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}
