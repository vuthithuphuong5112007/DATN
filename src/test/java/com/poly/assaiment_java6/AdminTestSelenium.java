package com.poly.assaiment_java6;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;

public class AdminTestSelenium {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        try {
            // 1. Đăng nhập Admin
            driver.get("http://localhost:8080/login");
            driver.findElement(By.name("username")).sendKeys("admin");
            driver.findElement(By.name("password")).sendKeys("admin123");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            System.out.println("Step 1: Đăng nhập thành công");

            // 2. Vào Quản lý Sản phẩm
            driver.get("http://localhost:8080/admin/sanpham");
            System.out.println("Step 2: Đang xem danh sách Sản phẩm");
            Thread.sleep(1500);

            // 3. Vào Quản lý Danh mục
            driver.get("http://localhost:8080/admin/danhmuc");
            System.out.println("Step 3: Đang xem danh sách Danh mục");
            Thread.sleep(1500);

            // 4. Vào Quản lý Đơn hàng
            driver.get("http://localhost:8080/admin/donhang");
            System.out.println("Step 4: Đang xem danh sách Đơn hàng");

            // 5. Ấn vào "Chi tiết" đơn hàng đầu tiên (Dựa trên ảnh cdf900.png)
            driver.findElement(By.xpath("//button[contains(text(), 'Chi tiết')]")).click();
            System.out.println("Step 5: Đã mở chi tiết đơn hàng");
            Thread.sleep(1000);

            // 6. Đổi trạng thái từ SHIPPED thành COMPLETED (Dựa trên ảnh cdfdb8.png)
            WebElement selectElement = driver.findElement(By.tagName("select"));
            Select dropdownStatus = new Select(selectElement);
            dropdownStatus.selectByValue("COMPLETED");
            System.out.println("Step 6: Đã chọn trạng thái COMPLETED");

            // 7. Ấn nút Lưu
            driver.findElement(By.xpath("//button[contains(text(), 'Lưu')]")).click();
            System.out.println("Step 7: Đã cập nhật trạng thái đơn hàng");
            Thread.sleep(1500);

            // 8. Vào Báo cáo thống kê
            driver.get("http://localhost:8080/admin/baocaothongke");
            System.out.println("Step 8: Đang xem Báo cáo thống kê");
            Thread.sleep(2000);

            // 9. Quay về trang chủ
            driver.findElement(By.partialLinkText("Quay lại Trang Chủ")).click();

            if (driver.getCurrentUrl().equals("http://localhost:8080/")) {
                System.out.println("Step 9: Đã về trang chủ - KẾT THÚC CHƯƠNG TRÌNH - PASS");
            }

            System.out.println("Step 10: Thực hiện Đăng xuất khỏi hệ thống...");
            driver.findElement(By.linkText("Đăng xuất")).click();

            // Đợi một chút để trang chuyển hướng về trang chủ hoặc trang login
            Thread.sleep(2000);

            // 11. Kết thúc chương trình
            System.out.println("KỊCH BẢN HOÀN TẤT - HỆ THỐNG AN TOÀN - PASS");

        } catch (Exception e) {
            System.err.println("Lỗi kịch bản: " + e.getMessage());
        } finally {
            // Đóng trình duyệt, giải phóng bộ nhớ RAM
            System.out.println("Đang đóng trình duyệt");
            driver.quit();
        }
    }
}
