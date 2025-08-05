package suiteTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.CSVUtils;
import utils.ExtentReportManager;

public class LoginTest {
    WebDriver driver;
    String navegador;

    @BeforeClass
    @Parameters("browser")
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

    @DataProvider(name = "credenciales")
    public Object[][] leerCredenciales() {
        // CSV debe tener 3 columnas: username,password,resultadoEsperado
        List<String[]> datos = CSVUtils.leerCSV("src/test/resources/User.csv");
        Object[][] resultado = new Object[datos.size()][3];
        for (int i = 0; i < datos.size(); i++) {
            resultado[i] = datos.get(i);
        }
        return resultado;
    }

    @Test(dataProvider = "credenciales")
    public void loginTest(String usuario, String clave, String resultadoEsperado) throws Exception {
        ExtentTest test = ExtentReportManager.createTest("Login con: " + usuario + " / " + clave);

        driver.get("https://practicetestautomation.com/practice-test-login/");

        // Limpiar y completar campos
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(usuario);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(clave);

        // Esperar y hacer clic en botón login
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement botonLogin = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonLogin);

        try {
            botonLogin.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonLogin);
        }

        Thread.sleep(1500); // esperar respuesta

        // Captura screenshot
        takeScreenshot("login_" + usuario + "_" + clave + ".png", test);

        // Verificación simplificada según resultado esperado
        boolean loginCorrecto;
        String mensajeError = "";

        // Detectar si se muestra mensaje de error
        try {
            WebElement mensajeErrorElem = driver.findElement(By.id("error"));
            mensajeError = mensajeErrorElem.getText();
        } catch (NoSuchElementException e) {
            mensajeError = "";
        }

        if (resultadoEsperado.equalsIgnoreCase("Login exitoso")) {
            // Validar que haya logout (login correcto)
            loginCorrecto = driver.findElements(By.xpath("//button[text()='Log out']")).size() > 0;
            if (loginCorrecto) {
                test.pass("Login exitoso para usuario: " + usuario);
            } else {
                test.fail("Se esperaba login exitoso pero no se encontró logout.");
            }
        } else {
            // Se espera error, validar que el mensaje de error aparezca y coincida con esperado (opcional)
            if (!mensajeError.isEmpty()) {
                test.pass("Login fallido esperado para usuario: " + usuario + " - Mensaje: " + mensajeError);
            } else {
                test.fail("Se esperaba error en login pero no se encontró mensaje de error.");
            }
        }

        // Volver a página de login para siguiente caso
        driver.get("https://practicetestautomation.com/practice-test-login/");
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
