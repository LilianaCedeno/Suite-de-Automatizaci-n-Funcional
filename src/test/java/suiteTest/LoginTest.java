package suiteTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentTest;

import utils.CSVUtils;
import utils.ExtentReportManager;

public class LoginTest {
    WebDriver driver;
    String navegador;
    ExtentTest test;

    @Parameters("browser")
    @BeforeClass
    public void setup(@Optional("chrome") String browser) {
        navegador = browser.toLowerCase();

        if (navegador.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-extensions", "--disable-popup-blocking", "--incognito");
            driver = new ChromeDriver(options);
        }

        driver.manage().window().maximize();
        ExtentReportManager.initReport();
    }

    @DataProvider(name = "credenciales")
    public Object[][] leerCredenciales() {
        List<String[]> datos = CSVUtils.leerCSV("src/test/resources/User.csv");
        Object[][] resultado = new Object[datos.size()][2];
        for (int i = 0; i < datos.size(); i++) {
            resultado[i] = datos.get(i);
        }
        return resultado;
    }

    @Test(dataProvider = "credenciales")
    public void loginTest(String usuario, String clave) throws Exception {
        test = ExtentReportManager.createTest("Login con: " + usuario + "/" + clave);

        driver.get("https://demoqa.com/login");

        driver.findElement(By.id("userName")).clear();
        driver.findElement(By.id("userName")).sendKeys(usuario);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(clave);

        // Espera explícita para el botón de login

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement botonLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("login")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonLogin);

        // Intento de clic en el botón de login
        test.info("Intentando iniciar sesión con usuario: " + usuario + " y clave: " + clave);
        
        try {
            botonLogin.click();
        } catch (ElementClickInterceptedException e) {
            test.warning("Clic interceptado, se realiza clic con JavaScript.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonLogin);
        }

        Thread.sleep(1500);
        takeScreenshot("login_" + usuario + "_" + clave + ".png");

        boolean loginCorrecto = driver.findElements(By.id("submit")).size() > 0 &&
                driver.findElement(By.id("submit")).getText().equalsIgnoreCase("Log out");

        if (loginCorrecto) {
            test.pass("Login exitoso para usuario: " + usuario);
        } else {
            test.fail("Login fallido para usuario: " + usuario);
        }

        driver.get("https://demoqa.com/login");
    }

    

    public void takeScreenshot(String nombre) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String ruta = "screenshots/" + nombre;
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