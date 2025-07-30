package suiteTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.AfterClass;   
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

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
            driver = new ChromeDriver();
        }

        driver.manage().window().maximize();
        ExtentReportManager.initReport(); // Iniciar reporte
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

        driver.findElement(By.id("login")).click();
        Thread.sleep(1500);

        takeScreenshot("login_" + usuario + "_" + clave + ".png");

        boolean loginCorrecto = driver.findElements(By.id("submit")).size() > 0 &&
                                driver.findElement(By.id("submit")).getText().equalsIgnoreCase("Log out");

        if (loginCorrecto) {
            test.pass("Login exitoso");
        } else {
            test.fail("Login fallido");
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
