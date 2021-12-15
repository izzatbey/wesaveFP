package com.wesavefp.wesaveFP.autoscanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class AppNavigation {
    WebDriver driver;
    final static String URL = "http://zap/UI/reports/action/generate/";
    final static String xpath = "//input[@id='title]";
    final static String title = "ZAP Scanning Report";
    final static String template = "traditional-json";
    final static String contexts = "Default Context";
    private static String site_url;
    final static String confidences = "Confirmed|High|Medium|Low";
    final static String risks = "High|Medium|Low|Informational";
    final static String fileNamePattern = "{{yyyy-MM-dd}}-ZAP-Report-[[site]]";
    final static String directory = "/home/hduser/Documents/WeSave/wesaveFP/output";
    final static String display = "false";

    public AppNavigation(WebDriver driver, String url) {
        this.driver = driver;
        this.driver.manage().timeouts().pageLoadTimeout(255, TimeUnit.SECONDS);
        this.driver.manage().timeouts().implicitlyWait(255,TimeUnit.SECONDS);
        this.site_url = url;
    }

    public void generateReport() {
        driver.get(URL);
        driver.findElement(By.id("title")).clear();
        driver.findElement(By.id("title")).sendKeys(title);
        driver.findElement(By.id("template")).clear();
        driver.findElement(By.id("template")).sendKeys(template);
        driver.findElement(By.id("contexts")).clear();
        driver.findElement(By.id("contexts")).sendKeys(contexts);
        driver.findElement(By.id("sites")).clear();
        driver.findElement(By.id("sites")).sendKeys(site_url);
        driver.findElement(By.id("includedConfidences")).clear();
        driver.findElement(By.id("includedConfidences")).sendKeys(confidences);
        driver.findElement(By.id("includedRisks")).clear();
        driver.findElement(By.id("includedRisks")).sendKeys(risks);
        driver.findElement(By.id("reportFileNamePattern")).clear();
        driver.findElement(By.id("reportFileNamePattern")).sendKeys(fileNamePattern);
        driver.findElement(By.id("reportDir")).clear();
        driver.findElement(By.id("reportDir")).sendKeys(directory);
        driver.findElement(By.id("display")).clear();
        driver.findElement(By.id("display")).sendKeys(display);
        driver.findElement(By.id("button")).click();
    }
}
