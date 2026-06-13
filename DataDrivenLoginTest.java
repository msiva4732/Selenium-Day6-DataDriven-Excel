import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ExcelUtils;
import java.io.IOException;
import java.time.Duration;

public class DataDrivenLoginTest {
	WebDriver driver;
	WebDriverWait wait;
	@BeforeMethod
	public void setup() {
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
		driver.get("https://the-internet.herokuapp.com/login");
	}
	@DataProvider(name = "LoginData")
	public Object[][] getLoginData() throws IOException {
		String excelpath = System.getProperty("user.dir") + "/TestData.xlsx";
		Object[][] data = ExcelUtils.getTableArray(excelpath, "Sheet1");
		return data;
	}
	@Test(dataProvider = "LoginData")
	public void loginTest(String username, String password) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.cssSelector("button[type='submit']")).click();
		String flashMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash"))).getText();
		if (username.equals("tomsmith") && password.equals("SuperSecretPassword!")) {
			Assert.assertTrue(flashMessage.contains("You logged into a secure area!"),"Valid login falied");
			System.out.println("PASS: Login successful for user: " + username);
		} else {
			Assert.assertTrue(flashMessage.contains("Your username is invalid!") || flashMessage.contains("Your password is invalid"),"Invalid login did not show error");
			System.out.println("PASS: Invalid login correctly failed for user: " + username);	
		}
	}
	@AfterMethod
	public void tearDown() {
		driver.quit();
	}
}
