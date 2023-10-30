package com.herokuapp.theinternet;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ExceptionsTestsUpdated {
    private WebDriver driver;

    @Parameters({ "browser" })
    @BeforeMethod(alwaysRun = true)
    private void setUp(@Optional("chrome") String browser) {
//		Create driver
        switch (browser) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
                driver = new ChromeDriver();
                break;

            case "firefox":
                System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
                driver = new FirefoxDriver();
                break;

            default:
                System.out.println("Do not know how to start " + browser + ", starting chrome instead");
                System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
                driver = new ChromeDriver();
                break;
        }

        driver.manage().window().setPosition(new Point(-1000,200));
        driver.manage().window().maximize();
    }

    @Test
    public void noSuchElementExceptionTest() {
        driver.get("https://practicetestautomation.com/practice-test-exceptions/");

        WebElement addButtonElement = driver.findElement(By.id("add_btn"));
        addButtonElement.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement row2Input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='row2']/input")));

        Assert.assertTrue(row2Input.isDisplayed(), "Row 2 is not displayed");
    }

    @Test
    public void elementNotInteractableExceptionTest() {
        driver.get("https://practicetestautomation.com/practice-test-exceptions/");

        WebElement addButtonElement = driver.findElement(By.id("add_btn"));
        addButtonElement.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement row2Input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='row2']/input")));

        row2Input.sendKeys("Sushi");

        // If the below locator will not be unique, error Not Interactable Element will be displayed (as the first element with name save is disabled):
        
        //WebElement saveButton = driver.findElement(By.name("Save"));
        WebElement saveButton = driver.findElement(By.xpath("//div[@id='row2']/button[@name='Save']"));
        saveButton.click();

        WebElement confirmationMessage = driver.findElement(By.id("confirmation"));
        String messageText = confirmationMessage.getText();
        Assert.assertEquals(messageText, "Row 2 was saved", "Confirmation message text is not expected");
    }

    @Test
    public void invalidElementStateExceptionTest() {
        driver.get("https://practicetestautomation.com/practice-test-exceptions/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement row1Input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='row1']/input")));
        // Edit btn interaction needs to be added,
        // otherwise at com.herokuapp.theinternet.ExceptionsTestsUpdated.invalidElementStateExceptionTest(ExceptionsTestsUpdated.java:94)
        WebElement editButton = driver.findElement(By.id("edit_btn"));
        editButton.click();

        wait.until(ExpectedConditions.elementToBeClickable(row1Input));
        row1Input.clear();

        row1Input.sendKeys("Pucio");

        WebElement saveButton = driver.findElement(By.id("save_btn"));
        saveButton.click();

        String value = row1Input.getAttribute("value");
        Assert.assertEquals(value, "Pucio", "Input 1 field value is not expected");

        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmation")));
        String messageText = confirmationMessage.getText();
        Assert.assertEquals(messageText, "Row 1 was saved", "Confirmation message text is not expected");
    }

    @Test
    public void staleElementReferenceExceptionTest() {
        driver.get("https://practicetestautomation.com/practice-test-exceptions/");

        WebElement addButtonElement = driver.findElement(By.id("add_btn"));
        addButtonElement.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("instructions"))),
                "Instructions are still displayed");
    }

    @Test
    public void timeoutExceptionTest() {
        driver.get("https://practicetestautomation.com/practice-test-exceptions/");

        WebElement addButtonElement = driver.findElement(By.id("add_btn"));
        addButtonElement.click();

        // with 3 second wait test will fail with Timeout exception
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));
        WebElement row2Input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='row2']/input")));

        Assert.assertTrue(row2Input.isDisplayed(), "Row 2 is not displayed");
    }

    @AfterMethod(alwaysRun = true)
    private void tearDown() {
        // Close browser
        driver.quit();
    }
}
