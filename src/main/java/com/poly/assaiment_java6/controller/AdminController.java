package com.poly.assaiment_java6.controller;


import com.poly.assaiment_java6.dto.MonthlyRevenueDTO;
import com.poly.assaiment_java6.dto.OrderStatusDTO;
import com.poly.assaiment_java6.dto.Top5SanPhamDTO;
import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.DonHangRepository;
import com.poly.assaiment_java6.repository.SanPhamRepository;
import com.poly.assaiment_java6.service.ActiveUserListener;
import com.poly.assaiment_java6.service.ChiTietDonHangService;
import com.poly.assaiment_java6.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private SanPhamRepository sanPhamRepo;
    @Autowired
    private DonHangRepository donHangRepo;
    @Autowired
    private ActiveUserListener activeUserListener;// Tiêm listener vào
    @Autowired
    private DonHangService donHangService;
    @Autowired
    ChiTietDonHangService chiTietService;

    // Gộp 2 hàm trùng lặp thành 1 hàm duy nhất cho trang Dashboard
    @GetMapping("/admin/home")
    public String dashboard(Model model) {
        // 1. Lấy tất cả sản phẩm (cho bảng hoặc danh sách nếu cần)
        List<SanPham> sanPhamList = sanPhamRepo.findAll();
        model.addAttribute("sanPhamList", sanPhamList);

        // 2. Lấy tổng doanh thu
        BigDecimal totalRevenue = donHangRepo.getTotalRevenue();
        model.addAttribute("revenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 3. Lấy số lượng đơn hàng mới (PENDING)
        long newOrders = donHangRepo.countByTrangThaiDonHang("PENDING");
        model.addAttribute("newOrdersCount", newOrders);

        // 4. Các thông số khác (nếu có)
        model.addAttribute("revenueChange", "+12%");

        // Lấy số lượng sản phẩm có tồn kho dưới 10
        long lowStockCount = sanPhamRepo.countLowStock(10);
        model.addAttribute("lowStock", lowStockCount);

        // Lấy số người đang online thực tế
        int onlineCount = activeUserListener.getTotalActiveUsers();
        model.addAttribute("onlineUsers", onlineCount);

        Pageable topFive = PageRequest.of(0, 5);
        List<DonHang> recentOrders = donHangRepo.findTop5RecentOrders(topFive);

        model.addAttribute("recentOrders", recentOrders);

        // lấy trạng thái đơn hàng theo kiểu biểu đồ bên dáhboard admin.html
        // 1. Xử lý dữ liệu doanh thu theo ngày
        LocalDate now = LocalDate.now();
        List<MonthlyRevenueDTO> revenueList = donHangService.getDailyRevenue(now.getMonthValue(), now.getYear());

        int daysInMonth = now.lengthOfMonth();
        Double[] dailyRevenue = new Double[daysInMonth];
        Arrays.fill(dailyRevenue, 0.0);
        for (MonthlyRevenueDTO dto : revenueList) {
            dailyRevenue[dto.getMonth() - 1] = dto.getRevenue();
        }
        model.addAttribute("revenueData", dailyRevenue);
        model.addAttribute("daysInMonth", daysInMonth);

        // 2. Xử lý dữ liệu biểu đồ tròn (Trạng thái)
        List<OrderStatusDTO> statusList = donHangService.getCountOrdersByStatus();
        List<String> statusLabels = statusList.stream().map(OrderStatusDTO::getStatus).toList();
        List<Long> statusCounts = statusList.stream().map(OrderStatusDTO::getCount).toList();

        model.addAttribute("statusLabels", statusLabels);
        model.addAttribute("statusCounts", statusCounts);

        // Tìm kiến top5 sản phẩm bán chạy
        List<Top5SanPhamDTO> top5 = chiTietService.getTop5BestSellers();

        // Tách tên và số lượng để Chart.js dễ đọc
        List<String> names = top5.stream().map(Top5SanPhamDTO::getTenSanPham).toList();
        List<Long> counts = top5.stream().map(Top5SanPhamDTO::getTongSoLuong).toList();

        model.addAttribute("topProductNames", names);
        model.addAttribute("topProductSales", counts);
        return "admin"; // Trả về templates/admin.html
    }

    @GetMapping("/admin/baocaothongke")
    public String baoCaoThongKe(Model model) {
        // 1. Lấy doanh thu từ Repository (Hàm bạn đã viết)
        BigDecimal totalRevenue = donHangRepo.getTotalRevenue();
        model.addAttribute("revenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 2. Lấy số lượng đơn thành công để hiện ở thẻ Success
        long completedCount = donHangRepo.countByTrangThaiDonHang("COMPLETED");
        model.addAttribute("completedOrdersCount", completedCount);

        long shippingCount = donHangRepo.countByTrangThaiDonHang("SHIPPED");
        model.addAttribute("shippingOrdersCount", shippingCount);

        long cancelledCount = donHangRepo.countByTrangThaiDonHang("CANCELLED");
        model.addAttribute("cancelledOrdersCount", cancelledCount);

        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        int daysInMonth = now.lengthOfMonth(); // Lấy số ngày thực tế của tháng hiện tại

        List<MonthlyRevenueDTO> list = donHangRepo.getDailyRevenue(month, year);

        Double[] dailyRevenue = new Double[daysInMonth];
        Arrays.fill(dailyRevenue, 0.0);

        for (MonthlyRevenueDTO dto : list) {
            dailyRevenue[dto.getMonth() - 1] = dto.getRevenue();
        }

        model.addAttribute("revenueData", dailyRevenue);
        model.addAttribute("daysInMonth", daysInMonth);
        return "admin/baocaothongke";
    }


}


// load ra trang sản phẩm