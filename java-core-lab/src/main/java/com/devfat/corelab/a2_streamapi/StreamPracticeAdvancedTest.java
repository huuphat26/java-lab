package com.devfat.corelab.a2_streamapi;

import org.testng.annotations.Test;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Phần 2: Các ví dụ Stream API "thực chiến" hơn — những pattern hay gặp
 * khi viết API Backend (validate request, phân trang, thống kê, build response...).
 * Dùng tiếp domain Product / Order ở bài trước, có thêm Customer để minh hoạ "join" dữ liệu.
 */
public class StreamPracticeAdvancedTest {

    private final List<Product> products = Arrays.asList(
            new Product("Laptop", "Electronics", 1000, 5),
            new Product("Phone", "Electronics", 500, 0),
            new Product("Shirt", "Clothing", 20, 10),
            new Product("Headphones", "Electronics", 100, 15)
    );

    private final List<Order> orders = Arrays.asList(
            new Order("HD01", "An", 500, "COMPLETED"),
            new Order("HD02", "Bình", 150, "PENDING"),
            new Order("HD03", "Cường", 1200, "COMPLETED"),
            new Order("HD04", "Dũng", 300, "CANCELLED")
    );

    /**
     * Bài 6: Tìm bản ghi "lớn nhất" bằng max() + xử lý Optional bằng orElseThrow.
     * Use case: lấy đơn hàng giá trị cao nhất để hiển thị "khách VIP" trên dashboard.
     */
    @Test
    public void findMaxOrderTest() {
        Order topOrder = orders.stream()
                .max(Comparator.comparingDouble(Order::getTotalAmount))
                .orElseThrow(() -> new NoSuchElementException("Không có đơn hàng nào"));
        System.out.println("Đơn hàng giá trị cao nhất: " + topOrder.getOrderId() + " - " + topOrder.getTotalAmount());
    }

    /**
     * Bài 7: DoubleSummaryStatistics - lấy min/max/avg/sum/count chỉ trong 1 lần duyệt.
     * Use case: build API thống kê (report) cho trang admin, tránh phải gọi 5 stream riêng lẻ.
     */
    @Test
    public void productStatisticsTest() {
        DoubleSummaryStatistics stats = products.stream()
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        System.out.println("Min: " + stats.getMin() + ", Max: " + stats.getMax()
                + ", Avg: " + stats.getAverage() + ", Tổng: " + stats.getSum());
    }

    /**
     * Bài 8: Collectors.partitioningBy - chia 1 list thành đúng 2 nhóm true/false.
     * Use case: tách đơn hàng "đủ điều kiện hoàn tiền" và "không đủ điều kiện" để xử lý 2 luồng nghiệp vụ khác nhau.
     */
    @Test
    public void partitionOrderTest() {
        Map<Boolean, List<Order>> partitioned = orders.stream()
                .collect(Collectors.partitioningBy(o -> o.getTotalAmount() >= 500));
        System.out.println("Đơn giá trị cao (>=500): " + partitioned.get(true).size());
        System.out.println("Đơn giá trị thấp (<500): " + partitioned.get(false).size());
    }

    /**
     * Bài 9: Collectors.toMap - dựng map tra cứu O(1) từ List, có xử lý key bị trùng.
     * Use case: load danh sách Product từ DB 1 lần rồi cache thành Map<name, Product> để tra cứu nhanh trong vòng lặp.
     */
    @Test
    public void buildProductLookupMapTest() {
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(
                        Product::getName,
                        Function.identity(),
                        (existing, duplicate) -> existing // nếu trùng key thì giữ bản ghi cũ
                ));
        Product laptop = productMap.get("Laptop");
        System.out.println("Tra cứu nhanh: " + laptop.getName() + " - " + laptop.getPrice());
    }

    /**
     * Bài 10: groupingBy + downstream Collectors.mapping - group rồi map sang field khác, không lấy cả object.
     * Use case: API trả về "tên sản phẩm theo từng danh mục" mà không cần lộ hết các field của entity.
     */
    @Test
    public void groupProductNamesByCategoryTest() {
        Map<String, List<String>> namesByCategory = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.mapping(Product::getName, Collectors.toList())
                ));
        System.out.println(namesByCategory);
    }

    /**
     * Bài 11: groupingBy + counting - đếm số lượng theo nhóm.
     * Use case: thống kê "số đơn hàng theo từng trạng thái" để vẽ chart trên dashboard quản lý.
     */
    @Test
    public void countOrdersByStatusTest() {
        Map<String, Long> countByStatus = orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        System.out.println(countByStatus);
    }

    /**
     * Bài 12: Collectors.joining - nối list thành 1 chuỗi có prefix/suffix/delimiter.
     * Use case: build chuỗi log, build câu IN (...) hiển thị, hoặc render response dạng text nhanh.
     */
    @Test
    public void buildOrderIdSummaryTest() {
        String idList = orders.stream()
                .map(Order::getOrderId)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Danh sách mã đơn: " + idList);
    }

    /**
     * Bài 13: allMatch / anyMatch / noneMatch - validate dữ liệu trước khi xử lý nghiệp vụ.
     * Use case: kiểm tra TẤT CẢ sản phẩm trong giỏ hàng còn tồn kho trước khi cho phép checkout (rất hay gặp ở service layer).
     */
    @Test
    public void validateStockBeforeCheckoutTest() {
        List<String> requestedProducts = List.of("Phone", "Shirt");
        boolean allInStock = products.stream()
                .filter(p -> requestedProducts.contains(p.getName()))
                .allMatch(p -> p.getStock() > 0);
        System.out.println("Đủ hàng để checkout: " + allInStock); // false vì Phone đã hết hàng (stock = 0)
    }

    /**
     * Bài 14: reduce với 3 tham số (identity, accumulator, combiner) - tự định nghĩa logic gộp.
     * Use case: tính doanh thu thực nhận sau khi trừ phí giao dịch, một phép tính mà sum() đơn giản không làm được.
     */
    @Test
    public void calculateNetRevenueTest() {
        double netRevenue = orders.stream()
                .filter(o -> o.getStatus().equalsIgnoreCase("COMPLETED"))
                .reduce(0.0,
                        (subtotal, order) -> subtotal + order.getTotalAmount() * 0.95, // trừ 5% phí cổng thanh toán
                        Double::sum);
        System.out.println("Doanh thu thực nhận: " + netRevenue);
    }

    /**
     * Bài 15: skip + limit - mô phỏng phân trang (pagination) ngay trên Stream.
     * Use case: khi cần phân trang trong-memory (sau khi đã query 1 lượng dữ liệu vừa phải từ DB), hoặc test nhanh logic phân trang.
     */
    @Test
    public void paginateOrdersTest() {
        int pageNumber = 1; // trang thứ 2 (đánh số từ 0)
        int pageSize = 2;
        List<Order> page = orders.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .toList();
        System.out.println("Trang " + (pageNumber + 1) + ": " + page.stream().map(Order::getOrderId).toList());
    }

    /**
     * Bài 16: Comparator.thenComparing - sắp xếp đa điều kiện (giá giảm dần, trùng giá thì xếp theo tên).
     * Use case: API "top N sản phẩm" với tiêu chí phụ rõ ràng để kết quả ổn định, không random thứ tự khi điểm bằng nhau.
     */
    @Test
    public void topExpensiveElectronicsTest() {
        List<String> top2 = products.stream()
                .filter(p -> p.getCategory().equals("Electronics"))
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed()
                        .thenComparing(Product::getName))
                .limit(2)
                .map(Product::getName)
                .toList();
        System.out.println("Top 2 Electronics đắt nhất: " + top2);
    }

    /**
     * Bài 17: flatMap + Objects::nonNull - gộp dữ liệu lồng nhau và tránh NPE với field có thể null.
     * Use case: mô phỏng "join" giữa Customer và danh sách orderId, dữ liệu thực tế từ DB rất hay có field null.
     */
    @Test
    public void joinCustomerOrderIdsTest() {
        List<Customer> customers = List.of(
                new Customer("An", List.of("HD01")),
                new Customer("Cường", List.of("HD03", "HD04")),
                new Customer("Eva", null) // khách hàng chưa từng đặt đơn -> field null, rất thường gặp
        );
        List<String> allOrderIds = customers.stream()
                .map(Customer::getOrderIds)
                .filter(Objects::nonNull) // chặn NPE trước khi flatMap
                .flatMap(List::stream)
                .toList();
        System.out.println("Toàn bộ mã đơn của các khách hàng: " + allOrderIds);
    }

    /**
     * Bài 18: Collectors.teeing (Java 12+) - tính 2 kết quả khác nhau chỉ trong 1 lần duyệt list.
     * Use case: vừa cần tổng doanh thu vừa cần số lượng đơn để tính trung bình, mà không muốn duyệt list 2 lần.
     */
    @Test
    public void teeingCollectorTest() {
        String result = orders.stream()
                .collect(Collectors.teeing(
                        Collectors.summingDouble(Order::getTotalAmount),
                        Collectors.counting(),
                        (sum, count) -> "Tổng: " + sum + " - Số đơn: " + count
                ));
        System.out.println(result);
    }

    /**
     * Bài 19: parallelStream - tăng tốc khi xử lý dataset lớn, kèm lưu ý quan trọng khi dùng trong thực tế.
     */
    @Test
    public void parallelStreamCautionTest() {
        double total = orders.parallelStream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        System.out.println("Tổng tiền tính bằng parallelStream: " + total);
        // ⚠️ Lưu ý thực tế:
        // - Chỉ nên parallelStream() khi dataset đủ lớn (thường > chục nghìn phần tử) và đã benchmark thực tế.
        // - Với vài chục/vài trăm phần tử như ở đây, overhead chia luồng còn tốn hơn lợi ích, nên chỉ là ví dụ minh hoạ.
        // - Tuyệt đối không dùng parallelStream() khi logic bên trong có side-effect (ví dụ: cộng vào 1 List chung)
        //   vì dễ gây lỗi race condition khó debug.
    }

    static class Product {
        String name;
        String category;
        double price;
        int stock;

        public Product(String name, String category, double price, int stock) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
    }

    static class Order {
        String orderId;
        String customerName;
        double totalAmount;
        String status;

        public Order(String orderId, String customerName, double totalAmount, String status) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.totalAmount = totalAmount;
            this.status = status;
        }

        public String getOrderId() { return orderId; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
    }

    static class Customer {
        String name;
        List<String> orderIds;

        public Customer(String name, List<String> orderIds) {
            this.name = name;
            this.orderIds = orderIds;
        }

        public List<String> getOrderIds() { return orderIds; }
    }
}
