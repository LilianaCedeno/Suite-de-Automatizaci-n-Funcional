package utils;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    // Inicializar solo si no existe
    public static void initReport(String reportPath) {
        if (extent == null) {
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            extent = new ExtentReports();
            extent.attachReporter(reporter);

            reporter.config().setReportName("Reporte de Automatización");
            reporter.config().setDocumentTitle("Resultados de Tests");

            extent.setSystemInfo("Sistema", System.getProperty("os.name"));
            extent.setSystemInfo("Usuario", System.getProperty("user.name"));
        }
    }

    public static void initReport() {
        initReport("reports/AutomationReport.html");
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }

    public static void logInfo(String message) {
        if (getTest() != null) getTest().info(message);
    }

    public static void logPass(String message) {
        if (getTest() != null) getTest().pass(message);
    }

    public static void logFail(String message) {
        if (getTest() != null) getTest().fail(message);
    }

    public static void logWarning(String message) {
        if (getTest() != null) getTest().warning(message);
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}