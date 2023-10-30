package com.herokuapp.theinternet;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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

public class ExceptionTest {

	private WebDriver driver;

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	private void setUp(@Optional("chrome") String browser) {
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
			System.out.println("Do not know to start " + browser + ", starting chrome insteaf");
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;
		}
		// maximize browser window
		driver.manage().window().maximize();

		// implicit wait
		// driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	@Test(priority = 1)
	public void notVisibleTest() {

		System.out.println("Starting notVisibleTest");

		String url = "https://the-internet.herokuapp.com/dynamic_loading/1";
		driver.get(url);
		System.out.println("Page dynamic_loading/1 is open");

		// WebElement startButton = driver.findElement(By.id("start"));
		WebElement startButton = driver.findElement(By.xpath("//div[@id='start']/button"));
		startButton.click();

		WebElement finishElement = driver.findElement(By.id("finish"));

		// Explicit wait (waiting for element)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(finishElement));
		String finishText = finishElement.getText();

		String expectedMessage = "Hello World!";
		Assert.assertTrue(finishText.contains(expectedMessage), "Actual message does not contain expected message."
				+ "\nFinish Text: " + finishText + "\nExpected Message: " + expectedMessage);

		// startButton.click();
	}

	@Test(priority = 2)
	public void timeoutTest() {

		String url = "https://the-internet.herokuapp.com/dynamic_loading/1";
		driver.get(url);
		System.out.println("Page dynamic_loading/1 is open");

		// WebElement startButton = driver.findElement(By.id("start"));
		WebElement startButton = driver.findElement(By.xpath("//div[@id='start']/button"));
		startButton.click();

		WebElement finishElement = driver.findElement(By.id("finish"));

		// Explicit wait (waiting for elemnt)
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		try {
			wait.until(ExpectedConditions.visibilityOf(finishElement));
		} catch (TimeoutException exception) {
			System.out.println("Exception catched: " + exception.getMessage());
			sleep(3000);
		}

		String finishText = finishElement.getText();

		String expectedMessage = "Hello World!";
		Assert.assertTrue(finishText.contains(expectedMessage), "Actual message does not contain expected message."
				+ "\nFinish Text: " + finishText + "\nExpected Message: " + expectedMessage);

		// startButton.click();
	}

	@Test(priority = 3)
	public void noSuchElementTest() {
		System.out.println("Starting noSuchElementTest");

		driver.get("https://the-internet.herokuapp.com/dynamic_loading/2");

		// WebElement startButton = driver.findElement(By.id("start"));
		WebElement startButton = driver.findElement(By.xpath("//div[@id='start']/button"));
		startButton.click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		Assert.assertTrue(
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("finish"), "Hello World!")),
				"Couldn't verify expected text 'Hello World!'");
	}

	@Test
	public void staleElementTest() {
		driver.get("https://the-internet.herokuapp.com/dynamic_controls");

		WebElement checkbox = driver.findElement(By.id("checkbox"));
		WebElement removeButton = driver.findElement(By.xpath("//button[contains(text(),'Remove')]"));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		removeButton.click();
		wait.until(ExpectedConditions.invisibilityOf(checkbox));

//		Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOf(checkbox)),
//				"Checkbox is still visible but should not be");

		// Assert.assertFalse(checkbox.isDisplayed());
		// Test fails because checkbox element is no longer on page. Fix to that is to
		// copy wait into assertion.
		// invisibilityOf returns Boolean == True when element is no longer visible

		// Another way how to handle stale element
		Assert.assertTrue(wait.until(ExpectedConditions.stalenessOf(checkbox)),
				"Checkbox is still visible but should not be");

		WebElement addButton = driver.findElement(By.xpath("//button[contains(text(),'Add')]"));
		addButton.click();

		//Test will fail, because for selenium new checkbox is not the same element as it was.
		//New element needs to be created or assigned, so instead of ExpectedConditions.visibilityOf(checkbox)
		//we create new reference and use:
		
		WebElement checkbox2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkbox")));
		Assert.assertTrue(checkbox2.isDisplayed(), "Checkbox is not visible but it should be");

	}
	
	@Test
	public void disabledElementTest() {
		driver.get("https://the-internet.herokuapp.com/dynamic_controls");
		WebElement enableButton = driver.findElement(By.xpath("//button[contains(text(),'Enable')]"));
		WebElement textField = driver.findElement(By.xpath("(//input)[2]"));
		
		
		enableButton.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(textField));
		
		textField.sendKeys("Hello World!");
		Assert.assertEquals(textField.getAttribute("value"), "Hello World!");
		Assert.assertTrue(
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "It's enabled!")),
				"Couldn't verify expected text 'It's enabled!'");
		
		
	}

	private void sleep(long m) {
		try {
			Thread.sleep(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@AfterMethod(alwaysRun = true)
	private void tearDown() {
		// Close Browser
		driver.quit();
	}

}
