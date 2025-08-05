package suiteTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import utils.CSVUtils;
import utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentTest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class RegisterTest {
    WebDriver driver;
    String navegador;

    @Parameters("browser")
    @BeforeClass
    public void setup(@Optional("chrome") String browser) {
        navegador = browser.toLowerCase();

        if (navegador.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-extensions", "--disable-popup-blocking", "--incognito");
            driver = new ChromeDriver(options);
        }

        driver.manage().window().maximize();
        ExtentReportManager.initReport();
    }

    @DataProvider(name = "usuarios")
    public Object[][] leerUsuarios() {
        List<String[]> datos = CSVUtils.registerLeerCSV("src/test/resources/Register.csv");
        Object[][] resultado = new Object[datos.size()][4];
        for (int i = 0; i < datos.size(); i++) {
            resultado[i] = datos.get(i);
        }
        return resultado;
    }

    @Test(dataProvider = "usuarios")
    public void registerTest(String nombre, String apellido, String username, String password) throws Exception {
        ExtentTest test = ExtentReportManager.createTest("Registro de usuario: " + username);

        try {
            driver.get("https://demoqa.com/register");

            driver.findElement(By.id("firstname")).clear();
            driver.findElement(By.id("firstname")).sendKeys(nombre);

            driver.findElement(By.id("lastname")).clear();
            driver.findElement(By.id("lastname")).sendKeys(apellido);

            driver.findElement(By.id("userName")).clear();
            driver.findElement(By.id("userName")).sendKeys(username);

            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys(password);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(By.id("register")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonRegistrar);

            try {
                botonRegistrar.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonRegistrar);
            }

            Thread.sleep(3000);
            takeScreenshot("register_" + username + ".png", test);

            boolean registroCorrecto = driver.findElements(By.id("submit")).size() > 0 &&
                    driver.findElement(By.id("submit")).getText().equalsIgnoreCase("Log out");

            if (registroCorrecto) {
                test.pass("Registro exitoso para el usuario: " + username);
            } else {
                test.fail("Registro fallido para el usuario: " + username);
            }

        } catch (Exception e) {
            test.fail("Error durante el registro: " + e.getMessage());
        } finally {
            driver.get("https://demoqa.com/register");
        }
    }

    public void takeScreenshot(String nombre, ExtentTest test) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String ruta = "screenshots/" + nombre;
            new File("screenshots").mkdirs();
            Files.copy(src.toPath(), Paths.get(ruta));
            test.addScreenCaptureFromPath(ruta);
        } catch (Exception e) {
            test.warning("No se pudo capturar pantalla");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
        ExtentReportManager.flushReport();
    }
}
