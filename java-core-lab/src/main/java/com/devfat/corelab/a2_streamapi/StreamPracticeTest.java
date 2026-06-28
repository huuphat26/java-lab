package com.devfat.corelab.a2_streamapi;

import org.testng.annotations.Test;
import java.util.*;
import java.util.stream.Collectors;

public class StreamPracticeTest {
    private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
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
    private final List<User> users = Arrays.asList(
            new User("alex", true, Arrays.asList("USER", "MANAGER")),
            new User("bob", false, List.of("ADMIN")),
            new User("charlie", true, Arrays.asList("USER", "ADMIN"))
    );
    /**
     * Bài 1: Cơ bản - Khởi tạo luồng, lọc số lớn hơn 2 và in trực tiếp.
     */
    @Test
    public void testStreamNumber() {
        numbers.stream()
                .filter(number -> number > 2)
                .forEach(System.out::println);
    }
    /**
     * Bài 2: Trung bình - Lọc sản phẩm công nghệ còn hàng, tính giá giảm và thu về List<String>.
     */
    @Test
    public void discountTest(){
        List<String> results = products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Electronics") && product.stock > 0)
                .map(product -> "Sản phẩm: " + product.name + " Giá giảm còn: " + (product.price * 0.9))
                .toList();
        System.out.println(results);
    }
    /**
     * Bài 3: Trung bình nâng cao - Thống kê và cộng dồn số lượng tồn kho theo từng danh mục.
     */
    @Test
    public void streamProductTest() {
        Map<String, Integer> stockByCategory = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,                    // 🏷️ Key: Danh mục sản phẩm
                        Collectors.summingInt(Product::getStock) // 🔢 Value: Tổng số tồn kho kiểu int
                ));
        System.out.println(stockByCategory);
    }
    /**
     * Bài 4: Thực tế Backend - Lọc hóa đơn thành công, sắp xếp giá giảm dần và ánh xạ sang DTO.
     */
    @Test
    public void testStreamOrder() {
        List<OrderDTO> orderResponse = orders.stream()
                .filter(order -> order.status.equalsIgnoreCase("COMPLETED"))
                .sorted(Comparator.comparingDouble(Order::getTotalAmount).reversed()) // 📶 Sắp xếp giảm dần
                .map(order -> new OrderDTO(order.orderId, "Khách hàng: " + order.customerName + " - Số tiền: " + order.totalAmount))
                .toList();
        System.out.println(orderResponse);
    }
    /**
     * Bài 5: Thực tế Backend - Trích xuất dữ liệu mảng con (flatMap), lọc trùng và sắp xếp bảng chữ cái.
     */
    @Test
    public void testStreamUser() {
        List<String> roles = users.stream()
                .filter(user -> user.active) // Đã chuẩn hóa tên biến từ role -> user
                .flatMap(user -> user.getRoles().stream()) // 🗺️ Trải phẳng danh sách roles
                .distinct() // 🛑 Loại bỏ các quyền trùng lặp
                .sorted()   // 📶 Sắp xếp tự động từ A-Z
                .toList();
        System.out.println(roles);
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

        public int getStock() { return stock; }
        public String getCategory() { return category; }
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

        public double getTotalAmount() { return totalAmount; }
    }

    static class OrderDTO {
        String id;
        String summary;

        public OrderDTO(String id, String summary) {
            this.id = id;
            this.summary = summary;
        }

        @Override
        public String toString() {
            return "OrderDTO{id='" + id + "', summary='" + summary + "'}";
        }
    }
    static class User {
        String username;
        boolean active;
        List<String> roles;

        public User(String username, boolean active, List<String> roles) {
            this.username = username;
            this.active = active;
            this.roles = roles;
        }
        public List<String> getRoles() { return roles; }
    }
}