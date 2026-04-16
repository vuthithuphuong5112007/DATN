package com.poly.assaiment_java6;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
public class VeloriaTestNG {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        // Để mỗi test case bắt đầu sạch sẽ từ trang chủ
        driver.get("http://localhost:8080/");
    }

    @Test(priority = 1) // Kịch bản 1: Admin quản lý đơn hàng
    public void testAdminFlow() throws InterruptedException {
        driver.get("http://localhost:8080/login");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(3000);

        driver.get("http://localhost:8080/admin/donhang");
        System.out.println("Đang kiểm tra đơn hàng Admin...");
        Thread.sleep(3000);
    }

    @Test(priority = 2) // Kịch bản 2: User mua nước hoa Giorgio Armani
    public void testUserPurchaseFlow() throws InterruptedException {
        driver.get("http://localhost:8080/login");
        driver.findElement(By.name("username")).sendKeys("fife");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(3000);

        driver.get("http://localhost:8080/products/detail/16");
        driver.findElement(By.className("add-to-cart-button")).click();
        System.out.println("User đã thêm hàng vào giỏ");
        Thread.sleep(3000);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
