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
import java.nio.file.Files; // Asegúrate de importar correctamente Files
import java.nio.file.Paths; // Asegúrate de importar correctamente Paths
import java.io.File;
import java.time.Duration;
import java.util.List;

public class RegisterTest {
    WebDriver driver;
    String navegador;
    ExtentTest test;

    // Método @Parameters para aceptar el parámetro 'browser' (chrome o firefox)
    @Parameters("browser")
    @BeforeClass
    public void setup(@Optional("chrome") String browser) {
        navegador = browser.toLowerCase();

        // Configuración del navegador
        if (navegador.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-extensions", "--disable-popup-blocking", "--incognito");
            driver = new ChromeDriver(options);
        }

        driver.manage().window().maximize();  // Maximizar la ventana del navegador
        ExtentReportManager.initReport();  // Inicializar el reporte
    }

    // Leer datos de registro desde el archivo CSV (Register.csv)
    @DataProvider(name = "usuarios")
    public Object[][] leerUsuarios() {
        List<String[]> datos = CSVUtils.registerLeerCSV("src/test/resources/Register.csv");
        Object[][] resultado = new Object[datos.size()][4];  // 4 parámetros: nombre, apellido, username, password
        for (int i = 0; i < datos.size(); i++) {
            resultado[i] = datos.get(i);  // Asignar las filas del CSV
        }
        return resultado;
    }

    // Test de registro de usuario
    @Test(dataProvider = "usuarios")
    public void registerTest(String nombre, String apellido, String username, String password) throws Exception {
        test = ExtentReportManager.createTest("Registro de usuario: " + username);

        driver.get("https://demoqa.com/register");

        // Completar el formulario de registro con los datos proporcionados
        driver.findElement(By.id("firstname")).clear();
        driver.findElement(By.id("firstname")).sendKeys(nombre);

        driver.findElement(By.id("lastname")).clear();
        driver.findElement(By.id("lastname")).sendKeys(apellido);

        driver.findElement(By.id("userName")).clear();
        driver.findElement(By.id("userName")).sendKeys(username);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);

        // Esperar que el botón de registro sea clickeable
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement botonRegistrar = wait.until(ExpectedConditions.elementToBeClickable(By.id("register")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botonRegistrar);

        try {
            botonRegistrar.click();  // Intentar hacer clic en el botón de registro
        } catch (ElementClickInterceptedException e) {
            // Si el clic es interceptado, realizar clic usando JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonRegistrar);
        }

        // Espera adicional para permitir que la página cargue
        Thread.sleep(3000);
        takeScreenshot("register_" + username + ".png");

        // Verificar si el registro fue exitoso
        boolean registroCorrecto = driver.findElements(By.id("submit")).size() > 0 &&
                driver.findElement(By.id("submit")).getText().equalsIgnoreCase("Log out");

        if (registroCorrecto) {
            test.pass("Registro exitoso para el usuario: " + username);
        } else {
            test.fail("Registro fallido para el usuario: " + username);
        }

        // Volver a la página de registro para el siguiente caso
        driver.get("https://demoqa.com/register");
    }

    // Método para capturar pantallas
    public void takeScreenshot(String nombre) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String ruta = "screenshots/" + nombre;
            Files.copy(src.toPath(), Paths.get(ruta));  // Copiar la imagen a la ruta
            test.addScreenCaptureFromPath(ruta);  // Agregar la captura al reporte
        } catch (Exception e) {
            test.warning("No se pudo capturar pantalla");
        }
    }

    // Cerrar el navegador y finalizar el reporte
    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();  // Cerrar el navegador
        ExtentReportManager.flushReport();  // Finalizar el reporte
    }
}
