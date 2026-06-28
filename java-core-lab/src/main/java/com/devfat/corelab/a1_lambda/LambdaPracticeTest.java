package com.devfat.corelab.a1_lambda;

import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.*;

public class LambdaPracticeTest {
    private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
    private final List<Integer> prices = Arrays.asList(20, 60, 30, 90, 150);
    private final List<String> emails = Arrays.asList("Alex@gmail.com", "admin@company.com", "Bob@yahoo.com", "STAFF@company.com");

    private final List<Employee> employees = Arrays.asList(
            new Employee("An", 5, 2000),
            new Employee("Bình", 3, 1500),
            new Employee("Cường", 4, 1800),
            new Employee("Dũng", 2, 1200)
    );

    /**
     * Bài 1: Cơ bản - Lọc số chẵn bằng Predicate và in ra bằng Consumer.
     */
    @Test
    public void numbersTest() {
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Consumer<Integer> consumer = System.out::println;

        for (Integer number : numbers) {
            if (isEven.test(number)) {
                consumer.accept(number);
            }
        }
    }

    /**
     * Bài 2: Trung bình - Lọc giá cao, định dạng tiền tệ và in chuỗi thông báo.
     */
    @Test
    public void pricesTest() {
        Predicate<Integer> isLargeThanFifty = n -> n > 50;
        Function<Integer, String> formatPrice = number -> String.format("Giá cao: %s USD ", number);
        Consumer<String> consumer = System.out::println;

        for (Integer price : prices) {
            if (isLargeThanFifty.test(price)) {
                consumer.accept(formatPrice.apply(price));
            }
        }
    }

    /**
     * Bài 3: Trung bình - Lọc loại bỏ email nội bộ công ty và chuẩn hóa chữ thường.
     */
    @Test
    public void emailsTest() {
        Predicate<String> isCompanyMail = email -> email.endsWith("@company.com");
        Function<String, String> formatEmail = email -> String.format("%s ", email.toLowerCase());
        Consumer<String> consumer = System.out::println;

        for (String email : emails) {
            if (!isCompanyMail.test(email)) {
                consumer.accept(formatEmail.apply(email));
            }
        }
    }
    /**
     * Bài 4: Ứng dụng thực tế - Lọc nhân viên xuất sắc để tính toán và tăng 15% lương.
     */
    @Test
    public void rewardTest() {
        Predicate<Employee> isBestEmployee = employee -> employee.performanceScore >= 4;
        Function<Employee, String> formatEmployee = employee -> "Chúc mừng: " + employee.name + ", lương mới là: " + (employee.salary * 1.15);
        Consumer<String> consumer = System.out::println;

        for (Employee employee : employees) {
            if (isBestEmployee.test(employee)) {
                consumer.accept(formatEmployee.apply(employee));
            }
        }
    }

    // =========================================================================
    // 🚀 BONUS CASES (Các kịch bản thực tế nâng cao hay gặp trong Backend)
    // =========================================================================

    /**
     * Bonus 1: Hệ thống Security - Kiểm tra tính hợp lệ của chuỗi Authorization Header Token.
     */
    @Test
    public void testTokenValidation() {
        List<String> headers = Arrays.asList("Bearer abc123xyz", "Basic dXNlcjpwYXNz", "Bearer qwe456rty");

        // Predicate kiểm tra chuỗi hợp lệ phải bắt đầu bằng từ khóa "Bearer "
        Predicate<String> isBearerToken = header -> header != null && header.startsWith("Bearer ");
        Consumer<String> logger = token -> System.out.println("[API Auth] Token hợp lệ: " + token);

        for (String header : headers) {
            if (isBearerToken.test(header)) {
                logger.accept(header);
            }
        }
    }

    /**
     * Bonus 2: Hệ thống Giao dịch - Tự động sinh mã Tracking ID ngẫu nhiên cho lịch sử đơn hàng.
     */
    @Test
    public void testTransactionGenerator() {
        // Supplier không nhận tham số đầu vào, chỉ có nhiệm vụ sinh và cung cấp dữ liệu (ở đây là chuỗi UUID)
        Supplier<String> trackingIdSupplier = () -> "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        System.out.println("--- Khởi tạo danh sách mã theo dõi giao dịch hệ thống ---");
        for (int i = 0; i < 3; i++) {
            String trackingId = trackingIdSupplier.get(); // Sử dụng phương thức .get()
            System.out.println("Đã tạo mã đơn hàng mới: " + trackingId);
        }
    }

    @Test
    public void testBiFunction() {
    BiFunction<String, Double, String> formatSalary =
            (name, salary) -> "Nhân viên: " + name + " - Lương: " + salary;
    String result = formatSalary.apply("An", 2000.0);
    System.out.println(result);
    }


    static class Employee {
        String name;
        int performanceScore;
        double salary;

        public Employee(String name, int performanceScore, double salary) {
            this.name = name;
            this.performanceScore = performanceScore;
            this.salary = salary;
        }
    }
}