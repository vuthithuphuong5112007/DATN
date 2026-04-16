package com.poly.assaiment_java6;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public class UserTestSelenium {
    public static void main(String[] args) {
        // 1. Khởi tạo trình duyệt
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        try {
            // Bước 1: Đăng nhập với tài khoản fife / 123
            driver.get("http://localhost:8080/login");
            driver.findElement(By.name("username")).sendKeys("fife");
            driver.findElement(By.name("password")).sendKeys("123");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            System.out.println("Đã đăng nhập tài khoản: fife");
            Thread.sleep(3000);

            // Bước 2: Nhấn MUA NGAY trên Banner (Ảnh ce13a6.jpg)
            driver.findElement(By.linkText("MUA NGAY")).click();
            Thread.sleep(3000);

            // Bước 3: Vào thẳng chi tiết sản phẩm Giorgio Armani (ID 16 - Ảnh ce1419.jpg)
            driver.get("http://localhost:8080/products/detail/16");
            System.out.println("Đang xem chi tiết Giorgio Armani");
            Thread.sleep(3000);

            // 4. Nhấn "Thêm vào Giỏ hàng" bằng Class (Dựa trên ảnh ce78bb.png)
            System.out.println("Đang tìm nút Thêm vào Giỏ hàng...");
            WebElement btnAddToCart = driver.findElement(By.className("add-to-cart-button"));
            btnAddToCart.click();
            System.out.println("Đã thêm sản phẩm vào giỏ");
            Thread.sleep(3000);

            // 5. Vào Giỏ hàng -> Thanh toán (ID 16, số lượng 3 theo ảnh ce1724.jpg)
            driver.get("http://localhost:8080/cart");
            driver.get("http://localhost:8080/checkout?items=16-1");
            Thread.sleep(3000);

            // 6. Nhấn HOÀN TẤT ĐẶT HÀNG (Ảnh ce6999.png)
            driver.findElement(By.xpath("//button[contains(text(), 'HOÀN TẤT ĐẶT HÀNG')]")).click();
            System.out.println("Đặt hàng hoàn tất!");
            Thread.sleep(3000);

            // 7. Kiểm tra đơn hàng tại /orders
            Thread.sleep(2000);
            System.out.println("URL hiện tại: " + driver.getCurrentUrl());
            Thread.sleep(3000);

            // 8. Đăng xuất (Ảnh ce6a59.png)
            driver.get("http://localhost:8080/logout");
            System.out.println("Đã đăng xuất hệ thống");
            Thread.sleep(3000);

        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        } finally {
            // 9. Tự động tắt màn hình
            System.out.println("Chương trình kết thúc. Đóng trình duyệt");
            driver.quit();
        }
    }
}
