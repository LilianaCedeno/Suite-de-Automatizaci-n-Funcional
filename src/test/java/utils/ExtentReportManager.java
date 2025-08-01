// package utils;

// import com.aventstack.extentreports.*;
// import com.aventstack.extentreports.reporter.ExtentSparkReporter;

// public class ExtentReportManager {
//     private static ExtentReports extent;
//     private static ExtentTest test;

//     public static void initReport() {
//         ExtentSparkReporter reporter = new ExtentSparkReporter("reports/LoginReport.html");
//         extent = new ExtentReports();
//         extent.attachReporter(reporter);
//     }

//     public static ExtentTest createTest(String testName) {
//         test = extent.createTest(testName);
//         return test;
//     }

//     public static void flushReport() {
//         if (extent != null) {
//             extent.flush();
//         }
//     }
// }

package utils;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static ExtentTest test;

    // Inicializa el reporte
    public static void initReport() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("reports/RegisterReport.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    // Crea un nuevo test dentro del reporte
    public static ExtentTest createTest(String testName) {
        test = extent.createTest(testName);
        return test;
    }

    // Finaliza y guarda el reporte
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    // Obtiene el objeto ExtentTest actual
    public static ExtentTest getTest() {
        return test;
    }
}
