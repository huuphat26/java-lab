# Tài Liệu Chuyên Sâu: Java Core & Concurrency cho Java Backend Developer (Spring Boot)

> Biên soạn dựa trên note keyword của bạn, mở rộng thêm phần nền tảng cần thiết để các khái niệm liên kết với nhau (Java Memory Model, Executor, JMM happens-before...). Mỗi mục đều có: **Khái niệm → Khi nào dùng → Code mẫu → Link tài liệu → Câu hỏi phỏng vấn**.

---

## Mục lục

0. [Lộ trình tổng quan & vì sao các keyword này quan trọng](#0)
1. [Java Core – nền tảng bắt buộc](#1)
2. [Concurrency vs Parallelism (Tính đồng thời vs Tính song song)](#2)
3. [Thread & Thread Pool](#3)
4. [ThreadLocal](#4)
5. [Stream API](#5)
6. [Lambda Expression](#6)
7. [Lock – Cơ chế khóa (tổng quan)](#7)
7b. [BONUS – Lock ở tầng Database & Distributed Lock](#7b)
8. [volatile](#8)
9. [synchronized vs ReentrantLock (Deep Dive + câu hỏi bonus)](#9)
10. [Java Memory Model & happens-before (kiến thức nền bổ trợ)](#10)
11. [CompletableFuture (Compatible Future)](#11)
12. [Các phiên bản JDK: 8, 11, 17, 21 (và 25)](#12)
13. [DDD – Domain-Driven Design trong dự án Spring Boot](#13)
14. [Bộ câu hỏi phỏng vấn tổng hợp theo cấp độ](#14)
15. [Tài liệu & lộ trình học tiếp theo](#15)
16. [BONUS – Cheat Sheet tổng hợp (ôn nhanh trước phỏng vấn)](#16)

---

<a id="0"></a>
## 0. Lộ trình tổng quan

Note của bạn thực ra phản ánh đúng 3 nhóm năng lực mà nhà tuyển dụng Java BE/Spring Boot hiện nay (2026) kiểm tra:

| Nhóm | Keyword của bạn | Vì sao quan trọng |
|---|---|---|
| **Core ngôn ngữ & tư duy hàm** | Lambda, Stream API | Code Spring Boot hiện đại viết theo phong cách functional, ít vòng lặp for thủ công |
| **Concurrency / Multithreading** | thread, ThreadLocal, lock, volatile, synchronized, ReentrantLock, CompletableFuture | Spring Boot service thường xử lý nhiều request đồng thời, cần hiểu race condition, deadlock, visibility |
| **Kiến trúc & version** | JDK 8/11/17/21, DDD | Quyết định cách tổ chức code, feature ngôn ngữ được dùng, và cách thiết kế hệ thống lớn |

**Gợi ý thứ tự học** (từ dễ đến khó, đúng thứ tự phụ thuộc kiến thức):

```
Java Core (OOP, Collection) 
   → Lambda 
   → Stream API 
   → Thread cơ bản 
   → Java Memory Model (visibility, happens-before) 
   → volatile 
   → synchronized 
   → Lock / ReentrantLock 
   → ThreadLocal 
   → Executor/ThreadPool 
   → CompletableFuture 
   → JDK version features (8 → 11 → 17 → 21) 
   → DDD (áp dụng vào thiết kế project Spring Boot)
```

> **Lưu ý bản cập nhật:** ngoài 12 mục gốc (1-13), tài liệu có thêm 3 mục **BONUS** không bắt buộc nhưng rất nên đọc vì liên kết trực tiếp tới các mục gốc và hay bị hỏi khi phỏng vấn BE: mục **7b** (Lock ở tầng Database & Distributed Lock), phần BONUS trong mục **3** và **9** (Virtual Thread pinning – JEP 491), và mục **16** (Cheat Sheet tổng hợp để ôn nhanh trước phỏng vấn).

---

<a id="1"></a>
## 1. Java Core – nền tảng bắt buộc

### Khái niệm
"Java Core" không phải một API cụ thể, mà là tập kiến thức nền: OOP (4 tính chất: đóng gói, kế thừa, đa hình, trừu tượng), Collection Framework (List/Set/Map, khi nào dùng ArrayList vs LinkedList, HashMap vs TreeMap vs LinkedHashMap), Exception Handling (checked vs unchecked), String pool & immutability, Generics, Autoboxing, Equals/HashCode contract, Java Memory (Stack vs Heap), Garbage Collection cơ bản.

### Khi nào cần nắm vững / áp dụng khi nào
- Đây là **điều kiện cần** trước khi học Spring Boot. Nhà tuyển dụng luôn hỏi Java Core trước, vì Spring chỉ là framework xây trên nền Java.
- Áp dụng hằng ngày: chọn cấu trúc dữ liệu đúng (ví dụ dùng `HashMap` để lookup O(1), dùng `TreeMap` khi cần dữ liệu có thứ tự), viết `equals()`/`hashCode()` đúng khi entity được dùng làm key trong Map hoặc trong Set.

### Code mẫu
```java
// equals/hashCode contract - rất hay bị hỏi khi entity dùng trong HashSet/HashMap
public class User {
    private final Long id;
    private final String email;

    public User(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
```

### Link tài liệu tham khảo
- Oracle Java Tutorials (chính thống): https://docs.oracle.com/javase/tutorial/java/index.html
- Oracle Collections Framework Overview: https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html
- Baeldung – Java Core series: https://www.baeldung.com/java-tutorial

### Câu hỏi phỏng vấn thường gặp
1. Phân biệt `ArrayList` và `LinkedList`, khi nào dùng loại nào?
2. `HashMap` hoạt động thế nào bên trong (bucket, hashCode, collision, resize)?
3. Vì sao phải override cả `equals()` và `hashCode()` cùng lúc?
4. Checked Exception và Unchecked Exception khác nhau thế nào? Khi nào nên tạo custom exception checked?
5. String, StringBuilder, StringBuffer khác nhau ở điểm nào? String pool hoạt động ra sao?
6. `==` và `.equals()` khác nhau thế nào với object và với String?


---

<a id="2"></a>
## 2. Concurrency vs Parallelism (Tính đồng thời vs Tính song song)

### Khái niệm
- **Concurrency (đồng thời)**: nhiều task được *quản lý* trong cùng một khoảng thời gian, nhưng không nhất thiết chạy *cùng lúc thực sự*. Trên 1 CPU lõi đơn, hệ điều hành/JVM chia nhỏ thời gian (time-slicing) để các thread thay nhau chạy, tạo cảm giác "đồng thời".
- **Parallelism (song song)**: nhiều task chạy **thực sự đồng thời** trên nhiều core/CPU vật lý khác nhau.
- Ví dụ dễ hiểu: 1 đầu bếp nấu 3 món bằng cách đảo qua đảo lại giữa các nồi = **concurrency**. 3 đầu bếp mỗi người nấu 1 món riêng = **parallelism**.
- Trong Java: `Thread`, `ExecutorService`, `CompletableFuture` thường dùng để đạt concurrency (xử lý nhiều việc, không block). `Stream.parallel()`, `ForkJoinPool` dùng để đạt parallelism (chia nhỏ 1 việc lớn ra nhiều core).

### Khi nào dùng / áp dụng khi nào
- Dùng **concurrency** khi: ứng dụng I/O-bound (gọi DB, gọi API khác, đọc file) – không cần nhiều CPU core, cần để các thread khác "rảnh tay" trong lúc chờ I/O. Đây là tình huống phổ biến nhất trong Spring Boot REST API (gọi DB, gọi service khác).
- Dùng **parallelism** khi: ứng dụng CPU-bound (tính toán nặng, xử lý batch dữ liệu lớn trong memory) – ví dụ xử lý 1 triệu record để tính tổng, áp dụng `parallelStream()`.

### Code mẫu
```java
// Concurrency: nhiều task I/O-bound chạy đồng thời, không cần nhiều core
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> callPaymentService());
executor.submit(() -> callShippingService());

// Parallelism: 1 tập dữ liệu lớn được chia nhỏ và xử lý trên nhiều core
List<Order> orders = orderRepository.findAll();
double total = orders.parallelStream()
        .mapToDouble(Order::getAmount)
        .sum();
```

### Link tài liệu tham khảo
- Oracle – Concurrency Tutorial: https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html
- Baeldung – Java Concurrency guide: https://www.baeldung.com/java-concurrency

### Câu hỏi phỏng vấn thường gặp
1. Phân biệt rõ Concurrency và Parallelism, cho ví dụ thực tế.
2. Một máy chỉ có 1 CPU core thì có đạt được parallelism không? Vì sao?
3. Khi nào nên dùng `parallelStream()` và khi nào tuyệt đối **không** nên dùng (gợi ý: task I/O-bound, dùng chung 1 `ForkJoinPool.commonPool()` với toàn hệ thống có thể gây nghẽn)?

---

<a id="3"></a>
## 3. Thread & Thread Pool

### Khái niệm
- **Thread**: đơn vị thực thi nhỏ nhất trong 1 process, có stack riêng nhưng chia sẻ heap với các thread khác trong cùng process. Tạo bằng cách extend `Thread` hoặc implement `Runnable`/`Callable`.
- Tạo Thread trực tiếp (`new Thread()`) rất tốn kém (mỗi thread tốn ~1MB stack mặc định, tốn chi phí context-switch của OS) → trong thực tế **không tạo thread tay** mà dùng **Thread Pool** qua `ExecutorService`.
- **Thread Pool**: tập hợp các thread được tạo sẵn, tái sử dụng để xử lý nhiều task, tránh chi phí tạo/hủy thread liên tục. Spring Boot dùng thread pool ở khắp nơi: Tomcat connector pool (xử lý HTTP request), `@Async` thread pool, DB connection pool (HikariCP đi kèm thread riêng để quản lý).

### Khi nào dùng / áp dụng khi nào
- Dùng Thread Pool khi cần xử lý nhiều task ngắn hạn lặp lại (xử lý request, gửi email bất đồng bộ, gọi nhiều API song song).
- Chọn loại pool theo bản chất tải:
  - `newFixedThreadPool(n)`: tải ổn định, biết trước số lượng task đồng thời tối đa (phù hợp CPU-bound, n ≈ số core).
  - `newCachedThreadPool()`: tải biến động ngắn hạn (cẩn thận: có thể tạo vô số thread → OOM nếu tải tăng đột biến).
  - `newScheduledThreadPool(n)`: chạy task định kỳ (cron-like job).
  - **Virtual Threads (Java 21, Project Loom)**: cho I/O-bound workload cực lớn (hàng trăm nghìn request đồng thời) mà không tốn nhiều OS thread thật.

### Code mẫu
```java
// Thread Pool cố định, dùng cho xử lý song song nhiều order
ExecutorService executor = Executors.newFixedThreadPool(4);

List<Future<OrderResult>> futures = orders.stream()
        .map(order -> executor.submit(() -> processOrder(order)))
        .collect(Collectors.toList());

for (Future<OrderResult> future : futures) {
    OrderResult result = future.get(); // blocking, chờ kết quả
}
executor.shutdown();

// Java 21 Virtual Thread - cho I/O-bound khối lượng cực lớn
try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
    virtualExecutor.submit(() -> callExternalApi());
}
```

### Link tài liệu tham khảo
- Oracle Thread API doc: https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
- Oracle ExecutorService doc: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
- Baeldung – Java Thread Pools: https://www.baeldung.com/thread-pool-java-and-guava
- Oracle JEP 444 – Virtual Threads (Java 21): https://openjdk.org/jeps/444
- Oracle JEP 491 – Synchronize Virtual Threads without Pinning (Java 24): https://openjdk.org/jeps/491

> **BONUS – cập nhật quan trọng (rất hay hỏi với ai biết Virtual Thread):** Từ Java 21 đến Java 23, nếu một Virtual Thread chạy vào block `synchronized` rồi bị block (gọi I/O, `sleep`...), nó sẽ bị **"pin" (ghim cứng)** vào carrier platform thread, không "unmount" được – làm mất hết lợi ích scale của Virtual Thread, vì carrier đó không phục vụ được Virtual Thread khác trong lúc bị ghim. Lý do kỹ thuật: JVM (trước Java 24) theo dõi quyền sở hữu monitor theo OS thread, nên nếu cho unmount giữa lúc giữ lock, một Virtual Thread khác mount lên cùng carrier đó có thể bị nhận nhầm là đang giữ lock – vỡ tính loại trừ lẫn nhau (mutual exclusion). Đây là lý do nhiều thư viện nổi tiếng (driver MySQL, PostgreSQL JDBC...) từng phải đổi từ `synchronized` sang `ReentrantLock` để tương thích tốt với Virtual Thread. **JEP 491 (Java 24)** đã sửa tận gốc: JVM đổi cách gắn quyền sở hữu monitor theo *Virtual Thread* thay vì theo carrier, nên từ Java 24 (và do đó Java 25 LTS) trở đi, `synchronized` không còn gây pinning trong các tình huống thông thường nữa – có thể dùng lại `synchronized` thoải mái mà không lo ảnh hưởng khả năng scale của Virtual Thread. Xem chi tiết đối chiếu `synchronized` ở mục 9.

### Câu hỏi phỏng vấn thường gặp
1. Vì sao không nên tự `new Thread()` cho từng request trong hệ thống production?
2. Phân biệt `Runnable` và `Callable` – cái nào trả về giá trị, cái nào throw exception?
3. `newFixedThreadPool` và `newCachedThreadPool` khác nhau thế nào, rủi ro của mỗi loại?
4. Thread có các trạng thái (state) nào? (NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED)
5. Virtual Thread (Java 21) giải quyết vấn đề gì so với Platform Thread truyền thống?
6. Khi `executor.shutdown()` được gọi, các task đang chạy có bị dừng giữa chừng không? Khác gì với `shutdownNow()`?
7. **(BONUS)** Virtual Thread + `synchronized` từng có vấn đề "pinning" gì trước Java 24? JEP 491 sửa bằng cách nào?


---

<a id="4"></a>
## 4. ThreadLocal

### Khái niệm
`ThreadLocal<T>` cho phép mỗi thread giữ **một bản sao riêng** của một biến, dù nhiều thread cùng truy cập vào cùng một instance `ThreadLocal`. Mỗi thread đọc/viết vào bản copy của chính nó, không ảnh hưởng tới thread khác → tránh việc phải synchronize khi không thực sự cần share dữ liệu giữa các thread.

Về bản chất bên trong, mỗi `Thread` object có một field `ThreadLocalMap` riêng, key là `ThreadLocal` instance, value là dữ liệu của thread đó.

### Khi nào dùng / áp dụng khi nào
- Trong Spring Boot, `ThreadLocal` là nền tảng của nhiều cơ chế quan trọng:
  - Lưu **request context** (ví dụ: `userId`, `traceId`, `tenantId`) để truyền xuyên suốt 1 request mà không cần truyền tham số qua từng method (`MDC` của Logback dùng `ThreadLocal` để lưu logging context).
  - `RequestContextHolder` của Spring lưu `HttpServletRequest` hiện tại theo `ThreadLocal`.
  - Lưu `Connection` DB riêng cho từng transaction (Spring's `TransactionSynchronizationManager`).
- **Cảnh báo quan trọng (rất hay bị hỏi)**: Khi dùng `ThreadLocal` cùng **Thread Pool** (Tomcat tái sử dụng thread để xử lý nhiều request khác nhau), nếu không gọi `remove()` sau khi xử lý xong, dữ liệu cũ có thể "rò" sang request tiếp theo dùng lại thread đó → bug khó debug, hoặc memory leak. **Luôn `remove()` trong `finally`**.

### Code mẫu
```java
public class RequestContext {
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();

    public static void setUserId(String userId) {
        userIdHolder.set(userId);
    }

    public static String getUserId() {
        return userIdHolder.get();
    }

    public static void clear() {
        userIdHolder.remove(); // QUAN TRỌNG khi dùng với thread pool
    }
}

// Trong 1 Filter của Spring Boot
public class UserContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        try {
            RequestContext.setUserId(req.getHeader("X-User-Id"));
            chain.doFilter(req, res);
        } finally {
            RequestContext.clear(); // tránh leak dữ liệu sang request sau dùng lại thread này
        }
    }
}
```

### Link tài liệu tham khảo
- Oracle ThreadLocal API doc: https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html
- Baeldung – An Introduction to ThreadLocal in Java: https://www.baeldung.com/java-threadlocal

### Câu hỏi phỏng vấn thường gặp
1. `ThreadLocal` hoạt động thế nào bên trong (ThreadLocalMap)?
2. Vì sao dùng `ThreadLocal` kết hợp Thread Pool có thể gây "leak" dữ liệu giữa các request? Cách phòng tránh?
3. Khác nhau giữa `ThreadLocal` và biến `static` thông thường?
4. `InheritableThreadLocal` dùng để làm gì, khác `ThreadLocal` ở điểm nào?
5. Cho ví dụ thực tế trong Spring Boot dùng `ThreadLocal` (gợi ý: MDC logging, security context `SecurityContextHolder`).

---

<a id="5"></a>
## 5. Stream API

### Khái niệm
Stream API (Java 8) cho phép xử lý collection theo phong cách **functional/declarative**: khai báo "muốn làm gì" (filter, map, sort, group...) thay vì viết vòng lặp thủ công ("làm thế nào"). Một Stream gồm:
- **Source**: nguồn dữ liệu (List, Set, Array...).
- **Intermediate operations** (lazy, trả về Stream mới): `filter`, `map`, `sorted`, `distinct`, `limit`...
- **Terminal operation** (kích hoạt thực thi pipeline): `collect`, `forEach`, `reduce`, `count`, `findFirst`...

Đặc điểm quan trọng: Stream **chỉ dùng được 1 lần** (gọi terminal operation lần 2 sẽ ném `IllegalStateException`), và các intermediate operation là **lazy** – chỉ thực thi khi có terminal operation.

### Khi nào dùng / áp dụng khi nào
- Dùng khi cần lọc/biến đổi/gom nhóm dữ liệu từ List/Set/Map – rất phổ biến khi xử lý kết quả trả về từ Repository trong Spring Boot (convert Entity → DTO, group theo field, tính tổng/đếm).
- **Không nên dùng** khi: logic quá phức tạp (nhiều điều kiện rẽ nhánh) làm code Stream khó đọc hơn for-loop thường; hoặc khi cần debug từng bước (for-loop dễ debug hơn).
- Dùng `parallelStream()` chỉ khi: tập dữ liệu đủ lớn, xử lý CPU-bound, và các phần tử độc lập (không side-effect, không cần thứ tự).

### Code mẫu
```java
List<Order> orders = orderRepository.findAll();

// filter + map + collect: lấy danh sách email khách có order > 1.000.000đ
List<String> emails = orders.stream()
        .filter(o -> o.getAmount() > 1_000_000)
        .map(o -> o.getCustomer().getEmail())
        .distinct()
        .collect(Collectors.toList());

// groupingBy: gom nhóm order theo trạng thái
Map<OrderStatus, List<Order>> byStatus = orders.stream()
        .collect(Collectors.groupingBy(Order::getStatus));

// reduce: tính tổng doanh thu
double totalRevenue = orders.stream()
        .mapToDouble(Order::getAmount)
        .sum();

// Optional kết hợp Stream
Optional<Order> highest = orders.stream()
        .max(Comparator.comparingDouble(Order::getAmount));
```

### Link tài liệu tham khảo
- Oracle Stream API doc: https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
- Baeldung – Introduction to Java Streams: https://www.baeldung.com/java-8-streams-introduction
- Baeldung – The Java 8 Stream API Tutorial (đầy đủ hơn): https://www.baeldung.com/java-8-streams

### Câu hỏi phỏng vấn thường gặp
1. Phân biệt **intermediate operation** và **terminal operation**? Cho ví dụ.
2. Tại sao Stream chỉ dùng được 1 lần? Điều gì xảy ra nếu gọi terminal operation 2 lần trên cùng 1 Stream?
3. `map()` và `flatMap()` khác nhau thế nào? Khi nào dùng `flatMap`?
4. `Stream` và `Collection` khác nhau cơ bản ở điểm nào?
5. Khi nào nên/không nên dùng `parallelStream()` trong môi trường production (đặc biệt là web server)?
6. `Collectors.groupingBy`, `Collectors.toMap`, `Collectors.partitioningBy` dùng khi nào?


---

<a id="6"></a>
## 6. Lambda Expression

### Khái niệm
Lambda expression (Java 8) là cách viết ngắn gọn để implement một **functional interface** (interface chỉ có đúng 1 abstract method, ví dụ `Runnable`, `Comparator`, `Function<T,R>`). Cú pháp: `(parameters) -> expression` hoặc `(parameters) -> { statements; }`. Lambda là nền tảng để Stream API, `CompletableFuture`, Spring's `@FunctionalInterface` bean (ví dụ định nghĩa `RouterFunction`, filter chain...) hoạt động ngắn gọn.

Các functional interface chuẩn quan trọng trong `java.util.function`: `Function<T,R>` (nhận T, trả R), `Predicate<T>` (nhận T, trả boolean), `Consumer<T>` (nhận T, không trả gì), `Supplier<T>` (không nhận gì, trả T), `BiFunction<T,U,R>`.

### Khi nào dùng / áp dụng khi nào
- Dùng lambda khi cần truyền 1 đoạn logic ngắn như tham số cho method (callback, comparator, predicate filter trong Stream).
- Không nên dùng lambda khi logic dài/phức tạp – nên tách ra method riêng (method reference `ClassName::methodName` để vẫn giữ code ngắn gọn nhưng dễ test, dễ đọc).
- Trong Spring Boot: dùng lambda khi viết `@Bean` cấu hình ngắn, viết `RestTemplate`/`WebClient` callback, viết Specification cho JPA Criteria, hoặc đăng ký filter/handler.

### Code mẫu
```java
// Lambda cơ bản thay cho anonymous class
Comparator<Order> byAmountDesc = (o1, o2) -> Double.compare(o2.getAmount(), o1.getAmount());

// Functional interface chuẩn
Function<Order, String> toEmail = order -> order.getCustomer().getEmail();
Predicate<Order> isLarge = order -> order.getAmount() > 1_000_000;
Consumer<Order> logOrder = order -> log.info("Order: {}", order.getId());

// Kết hợp Predicate
Predicate<Order> isLargeAndPending = isLarge.and(o -> o.getStatus() == OrderStatus.PENDING);

// Method reference - thường gọn hơn lambda khi chỉ gọi 1 method có sẵn
orders.forEach(System.out::println);
List<String> emails = orders.stream().map(Order::getCustomer).map(Customer::getEmail).toList();

// Custom functional interface
@FunctionalInterface
interface DiscountPolicy {
    double apply(double amount);
}
DiscountPolicy tenPercentOff = amount -> amount * 0.9;
```

### Link tài liệu tham khảo
- Oracle – Lambda Expressions Tutorial: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
- Oracle `java.util.function` package doc: https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html

### Câu hỏi phỏng vấn thường gặp
1. Functional Interface là gì? Vì sao lambda chỉ áp dụng được cho interface có đúng 1 abstract method?
2. Lambda có thể truy cập biến local bên ngoài không? Vì sao biến đó phải là "effectively final"?
3. Phân biệt lambda và anonymous inner class – khác nhau ở `this`, ở performance, ở khả năng có state riêng?
4. Method reference (`::`) có bao nhiêu loại? Cho ví dụ mỗi loại.
5. Khi nào nên tách lambda ra thành named method thay vì viết inline?

---

<a id="7"></a>
## 7. Lock – Cơ chế khoá (tổng quan)

### Khái niệm
"Lock" trong Java là cơ chế đảm bảo **tại một thời điểm chỉ một thread được truy cập vào một đoạn code/tài nguyên dùng chung (critical section)**, nhằm tránh **race condition** (kết quả sai do nhiều thread đọc/sửa dữ liệu cùng lúc không kiểm soát). Java cung cấp 2 nhóm cơ chế khoá chính:

1. **Intrinsic Lock (Monitor Lock)** – dùng từ khoá `synchronized`. Mỗi object trong Java đều có 1 "monitor" ẩn gắn liền với nó, dùng làm khoá.
2. **Explicit Lock** – dùng API `java.util.concurrent.locks.Lock`, triển khai phổ biến nhất là `ReentrantLock`. Ngoài ra còn `ReadWriteLock` (`ReentrantReadWriteLock` – cho phép nhiều reader đọc cùng lúc nhưng chỉ 1 writer), `StampedLock` (Java 8, hiệu năng cao hơn cho đọc nhiều/ghi ít).

Ngoài 2 cơ chế lock "độc quyền" trên, Java còn có cơ chế **không cần lock độc quyền**: biến `volatile` (đảm bảo visibility, không đảm bảo atomicity) và **Atomic classes** (`AtomicInteger`, `AtomicLong`...) dùng CAS (Compare-And-Swap) – non-blocking, hiệu năng cao hơn lock truyền thống cho các phép toán đơn giản (increment, compare-and-set).

### Khi nào dùng / áp dụng khi nào
- Dùng lock khi nhiều thread cùng đọc/ghi 1 tài nguyên dùng chung (biến static, cache trong memory, file, kết nối...).
- Ưu tiên thứ tự cân nhắc (từ đơn giản đến phức tạp):
  1. Tránh chia sẻ state nếu có thể (immutable object, mỗi thread tự có copy riêng qua `ThreadLocal`).
  2. Dùng các class thread-safe có sẵn (`ConcurrentHashMap`, `AtomicInteger`, `CopyOnWriteArrayList`) thay vì tự viết lock.
  3. Dùng `synchronized` nếu logic đơn giản, không cần tính năng nâng cao.
  4. Dùng `ReentrantLock`/`ReadWriteLock` khi cần `tryLock`, timeout, fairness, hoặc nhiều `Condition`.

### Code mẫu
```java
// 1. Intrinsic lock (synchronized)
public synchronized void increment() { count++; }

// 2. Explicit lock (ReentrantLock)
private final Lock lock = new ReentrantLock();
public void increment() {
    lock.lock();
    try {
        count++;
    } finally {
        lock.unlock();
    }
}

// 3. ReadWriteLock - nhiều reader, 1 writer
private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
public int read() {
    rwLock.readLock().lock();
    try { return cache.get("key"); } finally { rwLock.readLock().unlock(); }
}
public void write(int value) {
    rwLock.writeLock().lock();
    try { cache.put("key", value); } finally { rwLock.writeLock().unlock(); }
}

// 4. Lock-free, dùng Atomic (CAS) - thường nhanh hơn lock cho việc đơn giản
private final AtomicInteger counter = new AtomicInteger(0);
public void increment() { counter.incrementAndGet(); }
```

### Link tài liệu tham khảo
- Oracle – Lock Objects: https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html
- Oracle `Lock` interface doc: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/Lock.html
- Baeldung – Guide to java.util.concurrent.Locks: https://www.baeldung.com/java-concurrent-locks

### Câu hỏi phỏng vấn thường gặp
1. Race condition là gì? Cho ví dụ code bị race condition và cách fix.
2. Deadlock là gì? Điều kiện nào (4 điều kiện Coffman) dẫn tới deadlock? Cách phòng tránh trong code?
3. `ReadWriteLock` giải quyết vấn đề gì mà lock thường (mutex) không giải quyết tốt?
4. So sánh `synchronized`/`Lock` (blocking) với `Atomic` classes (non-blocking, CAS) – ưu nhược điểm mỗi loại?
5. Livelock và Starvation là gì, khác Deadlock thế nào?


---

<a id="7b"></a>
## 7b. BONUS – Lock ở tầng Database & Distributed Lock

> Mục này không nằm trong 12 keyword gốc của bạn, nhưng được thêm vào vì nó trả lời trực tiếp câu hỏi Senior ở mục 14 ("thiết kế service thanh toán idempotent, lock ở tầng nào") – và là chủ đề **rất hay bị hỏi với vai trò Java BE** vì nó cho thấy bạn hiểu lock không chỉ trong 1 JVM mà còn khi hệ thống chạy nhiều instance song song.

### Khái niệm
Tất cả lock ở mục 7 (`synchronized`, `ReentrantLock`, `Atomic`) chỉ có hiệu lực **trong 1 JVM**. Khi Spring Boot service của bạn được scale ra nhiều pod/instance (rất phổ biến trong production), 2 request trùng nhau có thể rơi vào 2 instance khác nhau – lock trong memory ở mục 7 **không bảo vệ được** trường hợp này. Cần phân biệt 3 cấp độ lock:

1. **Optimistic Lock** (lock lạc quan): không lock thật, chỉ kiểm tra version trước khi ghi – nếu version đã đổi (người khác ghi trước) thì fail và retry. Rẻ, nhanh, phù hợp khi tranh chấp (contention) thấp.
2. **Pessimistic Lock** (lock bi quan): lock thật ở tầng DB ngay khi đọc (`SELECT ... FOR UPDATE`), các transaction khác phải chờ. Phù hợp khi tranh chấp cao, chấp nhận đánh đổi performance để chắc chắn không đụng dữ liệu.
3. **Distributed Lock**: lock nằm ngoài cả DB và JVM, dùng 1 hệ thống trung tâm (thường là Redis) để nhiều instance cùng "thấy" và tôn trọng 1 lock chung – dùng khi cần lock nhanh, không muốn tốn 1 transaction DB chỉ để lock.

### Khi nào dùng / áp dụng khi nào
- **Optimistic Lock**: update profile người dùng, cập nhật thông tin ít bị 2 người sửa cùng lúc – ưu tiên vì rẻ, không giữ lock lâu.
- **Pessimistic Lock**: trừ tồn kho khi đặt hàng (2 người mua sản phẩm cuối cùng cùng lúc), nơi sai 1 lần là mất tiền/mất hàng thật.
- **Distributed Lock (Redis)**: chặn 2 request trùng lặp tới cùng 1 API thanh toán (idempotency), chạy 1 cron job nhưng chỉ muốn 1 instance trong cluster thực thi tại 1 thời điểm.

### Code mẫu
```java
// 1. Optimistic Lock - JPA tự check version, throw OptimisticLockException nếu đụng
@Entity
public class Product {
    @Id private Long id;
    private int stock;
    @Version private Long version;   // JPA tự tăng version, tự check khi UPDATE
}

// 2. Pessimistic Lock - lock ngay tại DB cho tới khi transaction commit
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}

// 3. Distributed Lock - Redis, dùng Redisson cho an toàn (tự xử lý hết hạn, hết tiến trình)
RLock lock = redissonClient.getLock("order-lock:" + orderId);
boolean acquired = lock.tryLock(500, 10_000, TimeUnit.MILLISECONDS); // chờ tối đa 500ms, giữ lock tối đa 10s
if (acquired) {
    try {
        processPayment(orderId);
    } finally {
        lock.unlock();
    }
} else {
    throw new IllegalStateException("Đơn hàng đang được xử lý, vui lòng thử lại");
}
```

### Link tài liệu tham khảo
- Baeldung – Optimistic Locking in JPA: https://www.baeldung.com/jpa-optimistic-locking
- Baeldung – Pessimistic Locking in JPA: https://www.baeldung.com/jpa-pessimistic-locking
- Redisson – Distributed Lock doc: https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers
- Martin Kleppmann – "How to do distributed locking" (bài phân tích rất nổi tiếng về rủi ro của Redis lock): https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html

### Câu hỏi phỏng vấn thường gặp
1. Phân biệt Optimistic Lock và Pessimistic Lock? Cho ví dụ thực tế mỗi loại.
2. Vì sao lock `synchronized`/`ReentrantLock` (mục 7, 9) không đủ khi service được scale ra nhiều instance?
3. Distributed Lock bằng Redis có rủi ro gì (gợi ý: hết hạn lock trước khi xử lý xong, mất kết nối Redis giữa lúc giữ lock, "split brain")? Vì sao nên dùng thư viện như Redisson thay vì tự viết `SETNX` tay?
4. **(Câu Senior ở mục 14)** Thiết kế API thanh toán idempotent khi 2 request trùng lặp tới cùng lúc – bạn chọn lock ở tầng nào (DB pessimistic lock hay Redis distributed lock) và vì sao?


---

<a id="8"></a>
## 8. volatile

### Khái niệm
`volatile` là một modifier áp dụng cho **biến** (field), không áp dụng được cho method hay class. Nó giải quyết vấn đề **visibility** (khả năng nhìn thấy) giữa các thread, **không giải quyết atomicity**.

Bình thường, mỗi thread/CPU core có thể cache giá trị biến vào local cache (CPU cache) để tăng performance, dẫn đến: thread A ghi giá trị mới, nhưng thread B vẫn đọc giá trị cũ trong cache của nó (visibility problem). Khai báo `volatile`:
- Mọi write vào biến volatile được ghi thẳng xuống main memory ngay lập tức (không giữ trong cache riêng của thread/CPU).
- Mọi read sẽ đọc trực tiếp từ main memory.
- Tạo ra quan hệ **happens-before**: mọi thay đổi *trước* lệnh ghi volatile sẽ "visible" với thread nào đọc volatile đó *sau*. Compiler/JVM cũng không được phép **reorder** các lệnh quanh biến volatile.

**volatile không đảm bảo atomicity**: với phép toán phức hợp (compound operation) như `count++` (gồm 3 bước: đọc – cộng – ghi), `volatile` không đủ để tránh race condition, vì 2 thread có thể đọc cùng giá trị cũ trước khi ai ghi lại.

### Khi nào dùng / áp dụng khi nào
- Dùng `volatile` khi: **1 thread viết, nhiều thread đọc**, và giá trị không cần phép toán phức hợp – ví dụ: cờ trạng thái (`volatile boolean running`) để dừng 1 thread từ thread khác, biến cấu hình được refresh runtime.
- **Không dùng** `volatile` khi cần tăng/giảm giá trị (increment/decrement) bởi nhiều thread → phải dùng `AtomicInteger` hoặc `synchronized`.
- Ứng dụng kinh điển: **Double-Checked Locking trong Singleton pattern** – field instance phải `volatile` để tránh thread khác nhìn thấy object "nửa khởi tạo" do reordering.

### Code mẫu
```java
public class TaskRunner {
    private volatile boolean running = true; // 1 thread đọc, 1 thread khác ghi

    public void run() {
        while (running) {
            // làm việc
        }
        System.out.println("Stopped");
    }

    public void stop() {
        running = false; // thread khác gọi để dừng - nhờ volatile, thay đổi này "visible" ngay
    }
}

// Double-checked locking Singleton - kinh điển khi hỏi volatile
public class ConnectionPool {
    private static volatile ConnectionPool instance;

    public static ConnectionPool getInstance() {
        if (instance == null) {                 // check 1 (không cần lock, nhanh)
            synchronized (ConnectionPool.class) {
                if (instance == null) {          // check 2 (trong lock, đảm bảo chỉ tạo 1 lần)
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }
}
```

### Link tài liệu tham khảo
- Oracle JLS – Memory Model / volatile: https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html
- Baeldung – Guide to the Volatile Keyword in Java: https://www.baeldung.com/java-volatile
- Baeldung – Volatile vs. Atomic Variables: https://www.baeldung.com/java-volatile-vs-atomic

### Câu hỏi phỏng vấn thường gặp
1. `volatile` giải quyết vấn đề gì? Nó **có** và **không** đảm bảo điều gì (visibility vs atomicity)?
2. Vì sao `volatile int count; count++;` trong nhiều thread vẫn bị race condition?
3. Giải thích tại sao trong Double-Checked Locking Singleton, field `instance` phải khai báo `volatile` (liên quan tới instruction reordering khi khởi tạo object)?
4. So sánh `volatile` với `synchronized` về mặt performance và tính năng?
5. happens-before là gì trong Java Memory Model, `volatile` tạo ra happens-before như thế nào?

---

<a id="9"></a>
## 9. synchronized vs ReentrantLock (Deep Dive)

### Khái niệm

**`synchronized`** là **intrinsic lock** built-in trong ngôn ngữ Java, gắn với monitor của object. Có 2 dạng:
```java
public synchronized void method() { ... }     // lock trên "this" (hoặc lock trên Class nếu là static method)
synchronized (someObject) { ... }              // lock trên 1 object cụ thể
```
JVM tự động `lock` khi vào block và tự động `unlock` khi ra khỏi block (kể cả khi có exception) → **không thể quên unlock**.

**`ReentrantLock`** (`java.util.concurrent.locks`, từ Java 5) là **explicit lock**, implement interface `Lock`, cho lập trình viên kiểm soát rõ ràng việc lock/unlock bằng code (`lock()`/`unlock()`). "Reentrant" nghĩa là 1 thread đã giữ lock có thể "vào lại" (acquire lại) chính lock đó nhiều lần mà không bị tự deadlock với chính nó (synchronized cũng có tính reentrant tương tự).

### Bảng so sánh chi tiết

| Tiêu chí | `synchronized` | `ReentrantLock` |
|---|---|---|
| Cách lock/unlock | Tự động (implicit), theo cấu trúc block | Thủ công (explicit) – **phải** `unlock()` trong `finally`, dễ quên gây deadlock vĩnh viễn nếu code sai |
| Phạm vi lock | Giới hạn trong 1 method/block, không thể lock ở method này và unlock ở method khác | Linh hoạt – có thể `lock()` ở 1 method, `unlock()` ở method khác (dù không khuyến khích) |
| `tryLock()` (thử lock, không block nếu không lấy được) | Không có | Có – `tryLock()`, `tryLock(timeout, unit)` |
| Interruptible lock (cho phép interrupt thread đang chờ lock) | Không | Có – `lockInterruptibly()` |
| Fairness (đảm bảo thread chờ lâu nhất được vào trước) | Không hỗ trợ | Có – `new ReentrantLock(true)` |
| Nhiều điều kiện chờ (`Condition`) | Chỉ có 1 "wait set" ẩn (`wait()`/`notify()`/`notifyAll()`) | Có thể tạo nhiều `Condition` riêng biệt cho từng tình huống chờ khác nhau |
| Biết được lock đang bị giữ bởi ai/đang có bao nhiêu thread chờ | Không (khó debug) | Có – `isLocked()`, `getQueueLength()`,... |
| Performance | JVM tối ưu hoá rất tốt qua các kỹ thuật (biased locking, lock coarsening, adaptive spinning) | Dùng CAS nội bộ, hiệu năng tốt, đặc biệt tốt khi có tranh chấp (contention) cao |
| Độ phức tạp code | Đơn giản, ít lỗi | Phức tạp hơn, dễ lỗi nếu quên `unlock()` |

### Khi nào dùng / áp dụng khi nào

**Dùng `synchronized` khi:**
- Logic đồng bộ đơn giản, không cần các tính năng nâng cao.
- Muốn code ngắn gọn, an toàn (không lo quên unlock).
- Hệ thống nhỏ/vừa, không có tranh chấp (contention) cao giữa nhiều thread.

**Dùng `ReentrantLock` khi:**
- Cần `tryLock()` để **không** block vô hạn (ví dụ: thử lấy lock trong X giây, nếu không được thì làm việc khác / trả lỗi cho client thay vì treo request).
- Cần **fairness** – tránh tình trạng 1 thread bị "đói" (starvation) vì các thread khác liên tục cướp lock.
- Cần nhiều điều kiện chờ khác nhau trên cùng 1 lock (ví dụ: cài đặt bounded buffer với 2 `Condition` riêng: "buffer đầy" và "buffer rỗng").
- Cần lock không theo cấu trúc block thông thường (ví dụ: lock node này, unlock node trước đó khi traverse linked list – "hand-over-hand locking").
- Cần khả năng debug/monitor (biết ai đang giữ lock, bao nhiêu thread đang chờ) trong hệ thống lớn.

> **Câu phỏng vấn bonus mà bạn note**: *"Sự khác nhau giữa synchronized và ReentrantLock. Khi nào và kịch bản nào sử dụng synchronized và reentrantlock"* — câu trả lời chuẩn nên đi theo đúng 2 phần: (1) liệt kê bảng so sánh tính năng như trên, và (2) đưa ra **kịch bản cụ thể**: ví dụ "Tôi dùng `synchronized` cho 1 method `updateBalance()` đơn giản trong service tài khoản vì logic ngắn và không cần tính năng nâng cao; nhưng tôi dùng `ReentrantLock` với `tryLock(500ms)` cho 1 critical section gọi tới external payment gateway, để nếu không lấy được lock trong 500ms thì trả lỗi 'hệ thống đang xử lý, vui lòng thử lại' thay vì để client chờ vô hạn."

### Code mẫu
```java
// Kịch bản 1: synchronized - đơn giản, đủ dùng
public class AccountService {
    private double balance;
    public synchronized void deposit(double amount) {
        balance += amount;
    }
}

// Kịch bản 2: ReentrantLock với tryLock + timeout - tránh treo request vô hạn
public class PaymentService {
    private final Lock lock = new ReentrantLock();

    public boolean processPayment(Order order) {
        try {
            if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    callExternalGateway(order);
                    return true;
                } finally {
                    lock.unlock();
                }
            } else {
                // không lấy được lock trong 500ms -> không block client, trả về luôn
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

// Kịch bản 3: ReentrantLock với nhiều Condition - Producer/Consumer
public class BoundedBuffer<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int capacity) { this.capacity = capacity; }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) notFull.await();
            queue.add(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
}
```

### Link tài liệu tham khảo
- Oracle `ReentrantLock` API doc: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html
- Oracle – Lock Objects tutorial: https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html
- Baeldung – Guide to java.util.concurrent.Locks: https://www.baeldung.com/java-concurrent-locks
- Oracle JEP 491 – Synchronize Virtual Threads without Pinning: https://openjdk.org/jeps/491

### BONUS – Virtual Thread đổi "luật chơi" giữa synchronized và ReentrantLock thế nào?

Bảng so sánh ở trên vẫn đúng, nhưng có 1 cập nhật quan trọng cần biết khi đi phỏng vấn 2025-2026: từ Java 21 đến 23, nếu code chạy trên **Virtual Thread** (không phải Platform Thread truyền thống), một lý do thực tế khiến nhiều team **ưu tiên `ReentrantLock` hơn `synchronized`** là vấn đề **pinning** – Virtual Thread bị "ghim cứng" vào carrier platform thread khi block trong `synchronized`, làm mất khả năng scale (xem chi tiết ở mục 3). `ReentrantLock` không có vấn đề này vì nó không dùng monitor của object.

Tuy nhiên, **Java 24 (JEP 491)** đã sửa tận gốc: `synchronized` không còn gây pinning với Virtual Thread trong các tình huống thông thường nữa. Điều này có nghĩa là từ Java 24/25 LTS trở đi, lý do "vì pinning" để chọn `ReentrantLock` không còn quan trọng như trước – lựa chọn giữa 2 cơ chế nên quay lại đúng các tiêu chí ở bảng so sánh trên (tryLock, fairness, Condition...) chứ không phải vì lo Virtual Thread bị ghim. Nếu hệ thống bạn đang/sẽ chạy Java 21-23 với Virtual Thread, đây vẫn là điểm cần lưu ý; nếu đã ở Java 24+ thì không còn là vấn đề.

### Câu hỏi phỏng vấn thường gặp
1. **(Câu bonus của bạn)** Sự khác nhau giữa `synchronized` và `ReentrantLock`? Khi nào dùng cái nào, cho kịch bản cụ thể?
2. Tại sao `ReentrantLock` gọi là "reentrant"? Nếu không có tính reentrant thì điều gì xảy ra khi 1 method synchronized gọi method synchronized khác trên cùng object?
3. Vì sao luôn phải gọi `unlock()` trong block `finally`? Điều gì xảy ra nếu quên?
4. `tryLock()` giải quyết bài toán gì mà `synchronized` không giải quyết được?
5. Giải thích "fairness" trong `ReentrantLock`. Fair lock có luôn tốt hơn unfair lock không? (Gợi ý: fair lock thường chậm hơn vì throughput thấp hơn).
6. `wait()/notify()/notifyAll()` (đi cùng `synchronized`) khác gì với `Condition.await()/signal()` (đi cùng `Lock`)?
7. **(BONUS)** Virtual Thread từng "bị ghim" (pinning) khi nào trong `synchronized`? Vì sao đây từng là lý do chọn `ReentrantLock`, và vì sao từ Java 24 lý do đó không còn quan trọng nữa?


---

<a id="10"></a>
## 10. Java Memory Model & happens-before (kiến thức nền bổ trợ)

### Khái niệm
Đây là kiến thức nền giải thích **vì sao** `volatile`, `synchronized`, `ReentrantLock` hoạt động được. Java Memory Model (JMM) định nghĩa cách các thread tương tác với memory thông qua khái niệm **happens-before**: nếu hành động A "happens-before" hành động B, thì kết quả của A được đảm bảo "visible" và có thứ tự trước B đối với mọi thread.

Các nguồn tạo happens-before phổ biến: lock/unlock cùng 1 monitor (`synchronized`), ghi/đọc cùng 1 biến `volatile`, `Thread.start()` happens-before mọi hành động trong thread đó, mọi hành động trong thread happens-before `Thread.join()` trả về.

### Khi nào cần hiểu
- Cần hiểu JMM để debug được các lỗi concurrency "ẩn" (code chạy đúng 99% thời gian nhưng đôi khi sai kết quả) – đây là loại bug khó nhất trong Java, không thể tìm ra bằng cách "đọc code thông thường" mà phải hiểu rõ về visibility/ordering.

### Link tài liệu tham khảo
- Oracle JLS Chapter 17 – Threads and Locks: https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html
- Baeldung – Java Memory Model: https://www.baeldung.com/java-memory-model

### Câu hỏi phỏng vấn thường gặp
1. happens-before là gì? Kể 3 nguồn tạo ra quan hệ happens-before trong Java.
2. Vì sao một biến không phải `volatile`, không có `synchronized` bảo vệ, có thể bị thread khác đọc giá trị "cũ" mãi mãi (infinite loop) trên 1 số JVM/CPU?

---

<a id="11"></a>
## 11. CompletableFuture (Compatible Future)

### Khái niệm
`CompletableFuture<T>` (Java 8, package `java.util.concurrent`) là phiên bản nâng cấp của `Future<T>` (Java 5), giải quyết các hạn chế lớn của `Future`:
- `Future.get()` là **blocking** – không có cách nào để "đăng ký callback" khi xong, phải tự ngồi chờ.
- `Future` không hỗ trợ **chain nhiều bước** (làm A xong thì làm B, xong thì làm C) hoặc **combine nhiều future** (chờ cả 2 future xong rồi gộp kết quả).
- `Future` không có cơ chế xử lý exception tốt.

`CompletableFuture` implement cả `Future` và `CompletionStage` → cho phép viết code bất đồng bộ (asynchronous), không-blocking, theo kiểu "pipeline" giống Stream API, với khả năng compose/combine/xử lý lỗi linh hoạt (khoảng 50 method).

Các method cốt lõi cần nhớ:
- Tạo: `supplyAsync(Supplier)` (có trả kết quả), `runAsync(Runnable)` (không trả kết quả).
- Chain: `thenApply` (transform kết quả, không async), `thenApplyAsync` (transform bất đồng bộ), `thenAccept` (consume kết quả, không trả gì), `thenCompose` (chain 2 CompletableFuture phụ thuộc nhau – giống `flatMap`).
- Combine: `thenCombine` (gộp 2 future độc lập), `allOf` (chờ tất cả xong), `anyOf` (chờ 1 trong nhiều future xong trước).
- Xử lý lỗi: `exceptionally`, `handle`, `whenComplete`.
- Lấy kết quả: `get()` (blocking, throw checked exception), `join()` (blocking, throw unchecked exception – thường dùng hơn trong lambda).

### Khi nào dùng / áp dụng khi nào
- Dùng khi cần gọi **nhiều service/API độc lập song song** rồi gộp kết quả – ví dụ trang chi tiết sản phẩm cần gọi đồng thời: service giá, service tồn kho, service review → dùng `CompletableFuture` + `allOf` thay vì gọi tuần tự (giảm latency tổng thể từ "tổng các API" xuống "API chậm nhất").
- Dùng khi cần xử lý **bất đồng bộ "fire and forget"** (gửi email, ghi log, gửi notification) mà không muốn block response trả về cho client.
- Trong Spring Boot, thường kết hợp với `@Async` (Spring tự quản lý executor) để method trả `CompletableFuture<T>` mà không cần tự tạo `ExecutorService`.
- **Lưu ý quan trọng**: luôn truyền **Executor tùy chỉnh** (không dùng `ForkJoinPool.commonPool()` mặc định) khi gọi blocking I/O (gọi DB, gọi API), vì commonPool dùng chung cho toàn JVM, nếu bị block bởi I/O sẽ ảnh hưởng tới cả những tác vụ CPU-bound khác đang dùng cùng pool.

### Code mẫu
```java
// Gọi 3 service độc lập song song, rồi gộp kết quả
ExecutorService executor = Executors.newFixedThreadPool(4);

CompletableFuture<Price> priceFuture = CompletableFuture.supplyAsync(
        () -> priceService.getPrice(productId), executor);
CompletableFuture<Integer> stockFuture = CompletableFuture.supplyAsync(
        () -> stockService.getStock(productId), executor);
CompletableFuture<List<Review>> reviewFuture = CompletableFuture.supplyAsync(
        () -> reviewService.getReviews(productId), executor);

CompletableFuture<ProductDetail> resultFuture = CompletableFuture
        .allOf(priceFuture, stockFuture, reviewFuture)
        .thenApply(v -> new ProductDetail(
                priceFuture.join(),
                stockFuture.join(),
                reviewFuture.join()));

ProductDetail detail = resultFuture.get(3, TimeUnit.SECONDS); // có timeout, tránh treo vô hạn

// Chain + xử lý lỗi
CompletableFuture.supplyAsync(() -> orderRepository.findById(orderId), executor)
        .thenApply(order -> order.getAmount())
        .thenAccept(amount -> log.info("Order amount: {}", amount))
        .exceptionally(ex -> {
            log.error("Failed to process order", ex);
            return null;
        });

// Spring Boot - dùng @Async để Spring tự quản lý executor
@Service
public class NotificationService {
    @Async
    public CompletableFuture<Void> sendEmailAsync(String to, String content) {
        emailClient.send(to, content);
        return CompletableFuture.completedFuture(null);
    }
}
```

### Link tài liệu tham khảo
- Oracle `CompletableFuture` API doc: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html
- Baeldung – Guide To CompletableFuture: https://www.baeldung.com/java-completablefuture
- Baeldung – CompletableFuture and ThreadPool: https://www.baeldung.com/java-completablefuture-threadpool
- Spring Docs – Task Execution and Scheduling (`@Async`): https://docs.spring.io/spring-framework/reference/integration/scheduling.html

### Câu hỏi phỏng vấn thường gặp
1. `CompletableFuture` giải quyết hạn chế gì của `Future` truyền thống?
2. Phân biệt `thenApply`, `thenApplyAsync`, `thenCompose`, `thenCombine` – khi nào dùng cái nào?
3. Khác nhau giữa `get()` và `join()`?
4. Vì sao không nên luôn dùng `ForkJoinPool.commonPool()` (executor mặc định) cho tác vụ blocking I/O trong `CompletableFuture`?
5. Cách xử lý exception trong chuỗi `CompletableFuture` – khác nhau giữa `exceptionally`, `handle`, `whenComplete`?
6. `allOf()` và `anyOf()` khác nhau thế nào? Khi nào dùng loại nào?
7. `@Async` của Spring liên quan gì tới `CompletableFuture`? Vì sao method `@Async` phải là `public` và được gọi từ bean khác (không tự gọi nội bộ – self-invocation issue do AOP proxy)?


---

<a id="12"></a>
## 12. Các phiên bản JDK: 8, 11, 17, 21 (và 25)

> Ghi chú: note của bạn ghi "JDK 8, 11, 17, 2" – mình hiểu đây là gõ thiếu, ý bạn là **21** (4 bản LTS – Long Term Support – kinh điển mà nhà tuyển dụng hay hỏi). Tính tới giữa 2026, **Java 25** (phát hành 9/2025) là bản LTS mới nhất, **Java 21** vẫn là LTS phổ biến nhất trong production. Java 8 vẫn còn rất nhiều hệ thống cũ (legacy) sử dụng.

### Khái niệm: LTS là gì?
Từ Java 9, Oracle phát hành phiên bản mới mỗi 6 tháng (tháng 3 và tháng 9), nhưng chỉ một số phiên bản được gọi là **LTS (Long-Term Support)** – được hỗ trợ vá lỗi/bảo mật trong nhiều năm, nên được dùng cho production. Các phiên bản LTS là Java 8, 11, 17, 21, và 25. Java 21 (LTS) phát hành tháng 9/2023, Java 25 (LTS) phát hành tháng 9/2025.

### Bảng tổng hợp feature quan trọng nhất với Backend dev

| Phiên bản | Năm | Feature quan trọng nhất cho Backend/Spring Boot dev |
|---|---|---|
| **Java 8** | 2014 | **Lambda Expression**, **Stream API**, `Optional<T>`, `default`/`static` method trong interface, **java.time** (LocalDate/LocalDateTime thay cho `Date`/`Calendar`), `CompletableFuture`, `Base64` API |
| **Java 11 (LTS)** | 2018 | `var` (type inference, từ Java 10), `HttpClient` mới (`java.net.http`) hỗ trợ async, các method String mới (`isBlank()`, `strip()`, `repeat()`), `Files.readString()/writeString()`, gỡ bỏ các module Java EE (JAXB, CORBA...) khỏi JDK |
| **Java 17 (LTS)** | 2021 | **Records** (class chỉ chứa data, immutable, tự sinh constructor/equals/hashCode – rất hợp làm DTO), **Sealed Classes** (giới hạn class nào được kế thừa), **Pattern Matching cho `switch`** (preview), Text Blocks (`"""`) cho chuỗi nhiều dòng |
| **Java 21 (LTS)** | 2023 | **Virtual Threads** (Project Loom – JEP 444, lập trình blocking-style nhưng scale như non-blocking, ảnh hưởng lớn tới thiết kế Spring Boot service), **Record Patterns**, **Pattern Matching for switch** (chính thức), **Sequenced Collections** (API lấy first/last element thống nhất) |
| **Java 24** | 2025 | **JEP 491 – Synchronize Virtual Threads without Pinning**: sửa lỗi Virtual Thread bị "ghim" khi block trong `synchronized` (xem mục 3, mục 9) – cải thiện đáng kể khả năng scale của Virtual Thread cho code dùng `synchronized` |
| **Java 25 (LTS, mới nhất 2025–2026)** | 2025 | **Structured Concurrency** (ổn định – quản lý nhóm các task con như 1 đơn vị, tự động cancel khi 1 task lỗi), kế thừa fix pinning từ JEP 491, tiếp tục cải tiến Virtual Threads, các tính năng liên quan Project Valhalla (value classes) |

### Khi nào dùng / áp dụng khi nào
- Dự án mới: nên target **Java 21 LTS** (được Spring Boot 3.x, hầu hết framework hỗ trợ tốt) hoặc Java 25 nếu muốn dùng tính năng mới nhất.
- Dự án legacy còn Java 8: cần biết rõ feature Java 8 (Lambda, Stream, Optional) vì đa số công ty Việt Nam vẫn còn maintain hệ thống Java 8/11.
- Khi đi phỏng vấn, nhà tuyển dụng thường hỏi: "Bạn biết Java mấy bản, từng dùng tính năng gì của bản mới?" → nên trả lời được ít nhất Lambda/Stream (8), `var`/HttpClient (11), Records/Sealed (17), Virtual Threads (21).

### Code mẫu (đại diện mỗi bản)
```java
// Java 8 - Lambda + Stream + Optional
Optional<Order> order = orders.stream().filter(o -> o.getId() == 1).findFirst();

// Java 11 - var + HttpClient
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder(URI.create("https://api.example.com")).build();

// Java 17 - Record làm DTO + Sealed interface
public record OrderDto(Long id, String customerName, double amount) {}

public sealed interface PaymentResult permits Success, Failure {}
public record Success(String transactionId) implements PaymentResult {}
public record Failure(String reason) implements PaymentResult {}

// Java 21 - Virtual Thread + Pattern Matching for switch
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> callExternalApi());
}

String describe(PaymentResult result) {
    return switch (result) {
        case Success s -> "OK: " + s.transactionId();
        case Failure f -> "FAIL: " + f.reason();
    };
}
```

### Link tài liệu tham khảo
- Oracle Java SE Support Roadmap (LTS chính thức): https://www.oracle.com/java/technologies/java-se-support-roadmap.html
- Java version history (Wikipedia, tổng hợp đầy đủ): https://en.wikipedia.org/wiki/Java_version_history
- JEP 444 – Virtual Threads: https://openjdk.org/jeps/444
- Marco Behler – A Guide to Java Versions and Features: https://www.marcobehler.com/guides/a-guide-to-java-versions-and-features

### Câu hỏi phỏng vấn thường gặp
1. Vì sao Java chuyển sang chu kỳ release 6 tháng/lần từ Java 9? LTS khác gì bản thường?
2. Kể 3 feature quan trọng nhất theo bạn của Java 8, và giải thích vì sao nó thay đổi cách viết code Java.
3. `record` trong Java 17 giải quyết vấn đề gì so với class POJO truyền thống (boilerplate constructor/getter/equals/hashCode)?
4. Virtual Thread (Java 21) khác Platform Thread (thread truyền thống) ở điểm nào? Nó giúp gì cho hệ thống xử lý nhiều request đồng thời?
5. Dự án của bạn (nếu có) đang dùng Java bản nào? Nếu upgrade lên bản mới hơn thì sẽ cải thiện được gì?

---

<a id="13"></a>
## 13. DDD – Domain-Driven Design trong dự án Spring Boot

### Khái niệm
DDD (Domain-Driven Design), khái niệm do Eric Evans đưa ra (2003), là phương pháp **thiết kế phần mềm xoay quanh nghiệp vụ (domain)** thay vì xoay quanh database hay framework. Mục tiêu: code phản ánh đúng ngôn ngữ và quy tắc nghiệp vụ thực tế (**Ubiquitous Language** – ngôn ngữ chung giữa dev và business).

Các khái niệm tactical (chi tiết) quan trọng nhất cần nắm:

| Khái niệm | Giải thích ngắn |
|---|---|
| **Entity** | Object có **identity** (id) riêng biệt, xuyên suốt vòng đời, dù thuộc tính thay đổi vẫn là "cùng 1 đối tượng" (ví dụ: `Order` với `orderId`) |
| **Value Object** | Object **không có identity**, định nghĩa bởi giá trị của nó, immutable (ví dụ: `Money`, `Address`, `Email`) – 2 Value Object có cùng giá trị thì coi là bằng nhau |
| **Aggregate** | Một nhóm Entity + Value Object được gom lại, đảm bảo **business invariant** (quy tắc nghiệp vụ luôn đúng) – ví dụ `Order` + `List<OrderItem>` là 1 aggregate, không cho phép sửa `OrderItem` mà không qua `Order` |
| **Aggregate Root** | Entity "đầu vào duy nhất" của 1 Aggregate – mọi truy cập từ bên ngoài vào Aggregate phải qua Aggregate Root (ví dụ: chỉ có thể `order.addItem()`, không cho `orderItemRepository.save()` trực tiếp) |
| **Repository** | Interface trừu tượng để lưu/lấy **toàn bộ Aggregate** (không phải lưu từng bảng), định nghĩa ở domain layer, implement ở infrastructure layer |
| **Domain Service** | Logic nghiệp vụ không tự nhiên thuộc về 1 Entity/Value Object cụ thể nào (ví dụ: tính phí ship dựa trên nhiều Aggregate khác nhau) |
| **Domain Event** | Sự kiện nghiệp vụ đã xảy ra (`OrderPlacedEvent`, `PaymentFailedEvent`) – dùng để các phần khác của hệ thống phản ứng lại (gửi email, cập nhật kho...) mà không coupling trực tiếp |
| **Bounded Context** | Ranh giới mà 1 model nghiệp vụ có nghĩa và nhất quán – ví dụ "Customer" trong context "Sales" khác với "Customer" trong context "Support" |
| **Rich Domain Model vs Anemic Domain Model** | Rich: business logic nằm *trong* Entity (ví dụ `order.confirm()` tự validate). Anemic: Entity chỉ có getter/setter, logic nằm hết ở Service – DDD khuyến nghị tránh Anemic Model |

### Khi nào dùng / áp dụng khi nào
- Áp dụng DDD khi: **domain nghiệp vụ phức tạp** (nhiều quy tắc, nhiều trạng thái, nhiều ràng buộc) – ví dụ hệ thống order/payment/inventory, ngân hàng, bảo hiểm. **Không cần** DDD cho CRUD đơn giản (DDD thêm boilerplate, không đáng nếu nghiệp vụ đơn giản).
- Trong Spring Boot, DDD thường kết hợp với kiến trúc **Layered** hoặc **Hexagonal/Ports & Adapters**:
  - `domain/`: Entity, Value Object, Aggregate, Repository interface, Domain Service – **không phụ thuộc Spring/JPA**.
  - `application/`: Use Case / Application Service – điều phối domain để thực hiện 1 nghiệp vụ cụ thể, quản lý transaction.
  - `infrastructure/`: implement Repository bằng JPA, gọi API ngoài, cấu hình Spring.
  - `presentation`/`web/`: Controller, DTO request/response.

### Code mẫu (cấu trúc Order Aggregate theo DDD trong Spring Boot)
```java
// ---- domain layer: KHÔNG phụ thuộc Spring/JPA ----
public class Order {                  // Aggregate Root (Entity)
    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;

    public static Order create(CustomerId customerId) {
        Order order = new Order(OrderId.generate(), customerId);
        order.status = OrderStatus.PENDING;
        return order;
    }

    // Business logic nằm TRONG Entity (Rich Domain Model)
    public void addItem(ProductId productId, Money unitPrice, int quantity) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Không thể thêm item vào order đã confirm");
        }
        items.add(new OrderItem(productId, unitPrice, quantity));
    }

    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Không thể confirm order rỗng");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public Money getTotalAmount() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.ZERO, Money::add);
    }
    // constructor, getter... (rút gọn)
}

public record Money(BigDecimal amount) {     // Value Object - immutable, không có identity
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    public Money add(Money other) { return new Money(amount.add(other.amount)); }
}

public interface OrderRepository {           // Repository interface định nghĩa ở domain
    void save(Order order);
    Optional<Order> findById(OrderId id);
}

// ---- application layer ----
@Service
public class PlaceOrderUseCase {
    private final OrderRepository orderRepository; // phụ thuộc INTERFACE, không phụ thuộc JPA

    public PlaceOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderId execute(PlaceOrderCommand command) {
        Order order = Order.create(command.customerId());
        command.items().forEach(item ->
                order.addItem(item.productId(), item.unitPrice(), item.quantity()));
        order.confirm();
        orderRepository.save(order);
        return order.getId();
    }
}

// ---- infrastructure layer: implement Repository bằng JPA ----
@Repository
public class JpaOrderRepository implements OrderRepository {
    private final OrderJpaEntity.JpaRepo jpaRepo;
    @Override
    public void save(Order order) { jpaRepo.save(OrderMapper.toEntity(order)); }
    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepo.findById(id.value()).map(OrderMapper::toDomain);
    }
}
```

### Link tài liệu tham khảo
- Martin Fowler – Domain-Driven Design (bliki): https://martinfowler.com/bliki/DomainDrivenDesign.html
- Martin Fowler – Anemic Domain Model (anti-pattern cần tránh): https://martinfowler.com/bliki/AnemicDomainModel.html
- Baeldung – Hexagonal Architecture, DDD, and Spring: https://www.baeldung.com/hexagonal-architecture-ddd-spring
- Eric Evans – sách gốc "Domain-Driven Design: Tackling Complexity in the Heart of Software" (tài liệu nền tảng nhất, nên đọc khi đã quen code DDD cơ bản)

### Câu hỏi phỏng vấn thường gặp
1. Phân biệt **Entity** và **Value Object**? Cho ví dụ trong domain bạn từng làm.
2. **Aggregate** và **Aggregate Root** là gì? Vì sao mọi truy cập phải đi qua Aggregate Root?
3. **Rich Domain Model** và **Anemic Domain Model** khác nhau thế nào? Vì sao Anemic Model bị coi là anti-pattern trong DDD (dù vẫn rất phổ biến thực tế)?
4. **Repository pattern** trong DDD khác gì với `JpaRepository` của Spring Data? (Gợi ý: Repository trong DDD là *abstraction nghiệp vụ*, định nghĩa ở domain layer; `JpaRepository` là *chi tiết kỹ thuật*, nên đặt ở infrastructure layer và implement interface domain).
5. **Bounded Context** là gì? Cho ví dụ về 1 khái niệm có nghĩa khác nhau ở 2 Bounded Context khác nhau trong cùng công ty.
6. DDD liên hệ gì với kiến trúc Hexagonal/Clean Architecture? CQRS là gì, có bắt buộc phải đi cùng DDD không?


---

<a id="14"></a>
## 14. Bộ câu hỏi phỏng vấn tổng hợp theo cấp độ

### Mức Fresher / Junior (nắm khái niệm, code được ví dụ cơ bản)
1. Thread là gì? Cách tạo thread trong Java (2 cách)?
2. Lambda expression là gì, viết được 1 ví dụ dùng với `Comparator`?
3. Stream API dùng để làm gì, các bước cơ bản của 1 pipeline Stream?
4. `volatile` dùng để làm gì?
5. Sự khác nhau cơ bản giữa `synchronized` method và `synchronized` block?

### Mức Mid-level (hiểu sâu, so sánh, biết trade-off)
1. Race condition, Deadlock là gì? Cách phát hiện và phòng tránh?
2. So sánh chi tiết `synchronized` và `ReentrantLock`, đưa ra kịch bản dùng từng loại.
3. `ThreadLocal` kết hợp Thread Pool có vấn đề gì? Cách xử lý?
4. `CompletableFuture` giải quyết vấn đề gì so với `Future`? Viết code gọi 2 service song song rồi gộp kết quả.
5. Phân biệt Concurrency và Parallelism, ví dụ thực tế trong hệ thống bạn từng làm.
6. Virtual Thread (Java 21) là gì, giải quyết bài toán gì?
7. Entity vs Value Object trong DDD, cho ví dụ cụ thể.
8. **(BONUS)** Phân biệt Optimistic Lock và Pessimistic Lock trong JPA? Khi nào dùng loại nào?

### Mức Senior (thiết kế hệ thống, đánh đổi kiến trúc)
1. Thiết kế 1 service xử lý thanh toán cần đảm bảo idempotent và tránh race condition khi 2 request trùng lặp tới cùng lúc – bạn sẽ dùng lock ở tầng nào (DB lock, distributed lock như Redis, hay in-memory lock)? Vì sao? **(xem mục 7b)**
2. Trong hệ thống microservices, `ThreadLocal` để lưu context (traceId, userId) có hoạt động đúng khi request đi qua nhiều service không? Cần kỹ thuật gì để propagate context xuyên service (gợi ý: header truyền đi, MDC + Sleuth/OpenTelemetry)?
3. Khi nào bạn chọn thiết kế theo DDD + Hexagonal Architecture, khi nào chọn kiến trúc Layered đơn giản (transaction script)? Trade-off giữa độ phức tạp và khả năng maintain?
4. Migrate hệ thống từ Java 8 lên Java 21 cần lưu ý gì (breaking change, performance, Virtual Thread ảnh hưởng thế nào tới thread pool config hiện tại)?
5. Thiết kế cơ chế cache trong service đa luồng cao – chọn `ConcurrentHashMap`, `ReadWriteLock`, hay cache library (Caffeine) – đánh đổi ra sao?
6. **(BONUS)** Hệ thống của bạn đang chạy Virtual Thread trên Java 21 và có nhiều `synchronized` block legacy gọi I/O bên trong – bạn sẽ làm gì để chẩn đoán và giảm pinning, và việc upgrade lên Java 24+ thay đổi quyết định đó thế nào?

---

<a id="15"></a>
## 15. Tài liệu & lộ trình học tiếp theo

### Tài liệu chính thống nên bookmark
- **Oracle Java Tutorials** (gốc, chính xác nhất): https://docs.oracle.com/javase/tutorial/
- **Oracle Java Concurrency Tutorial**: https://docs.oracle.com/javase/tutorial/essential/concurrency/
- **Baeldung** (rất nhiều bài chuyên sâu, ví dụ code đầy đủ, gần như "Stack Overflow có hệ thống"): https://www.baeldung.com/
- **Java Language Specification (JLS)** – khi cần hiểu cực sâu (vd: Memory Model): https://docs.oracle.com/javase/specs/
- **Martin Fowler's blog/bliki** – cho kiến trúc, DDD, design pattern: https://martinfowler.com/

### Gợi ý lộ trình thực hành (không chỉ đọc lý thuyết)
1. Viết 1 project nhỏ (ví dụ: hệ thống đặt vé xem phim) **không dùng Spring** trước, chỉ Java thuần, để tự tay code: Thread Pool xử lý đặt vé đồng thời, dùng `ReentrantLock`/`synchronized` để tránh 2 người đặt trùng 1 ghế (race condition kinh điển, hay được hỏi khi phỏng vấn).
2. Refactor project đó áp dụng DDD: tách `domain` (Seat, Booking, Showtime là Entity/Value Object), `application` (BookSeatUseCase).
3. Viết lại bằng Spring Boot, thêm `CompletableFuture`/`@Async` để gửi email xác nhận không block response.
4. Thử nâng cấp lên Java 21, đổi `ExecutorService` thường sang Virtual Thread, so sánh throughput.
5. Luyện trả lời các câu hỏi phỏng vấn ở mục 14 — đặc biệt câu **synchronized vs ReentrantLock** bạn note, hãy chuẩn bị sẵn 1 kịch bản thực tế cụ thể của riêng bạn (không chỉ liệt kê lý thuyết) vì nhà tuyển dụng senior rất thích hỏi "cho tôi 1 ví dụ bạn từng áp dụng".

---

<a id="16"></a>
## 16. BONUS – Cheat Sheet tổng hợp (ôn nhanh trước phỏng vấn)

> Dùng mục này **đêm trước phỏng vấn** hoặc khi cần ôn lại nhanh trong 10-15 phút — không phải để học lần đầu (học lần đầu thì đọc đủ các mục 1-13 ở trên). Mỗi dòng: định nghĩa 1 câu + khi dùng + lỗi hay gặp + câu trả lời chốt khi bị hỏi.

| Keyword | Định nghĩa 1 câu | Khi dùng | Lỗi/bẫy hay gặp | Câu trả lời chốt |
|---|---|---|---|---|
| **Concurrency vs Parallelism** | Đồng thời (quản lý nhiều task) vs song song (chạy thật cùng lúc trên nhiều core) | I/O-bound → concurrency; CPU-bound → parallelism | Tưởng 1 core vẫn "song song" được | "1 core chỉ time-slicing, không có parallelism thật" |
| **Thread/ThreadPool** | Đơn vị thực thi; Pool tái sử dụng thread tránh tốn chi phí tạo/hủy | Task I/O ngắn hạn lặp lại | `new Thread()` tay cho mỗi request → tốn tài nguyên | "Không tự tạo Thread, dùng ExecutorService/Virtual Thread" |
| **ThreadLocal** | Mỗi thread giữ 1 bản riêng của biến | Lưu context (traceId, userId) theo request | Quên `.remove()` khi dùng với Thread Pool → leak dữ liệu sang request sau | "Luôn remove() trong finally khi dùng với thread pool" |
| **Stream API** | Pipeline xử lý collection kiểu khai báo (filter→map→collect) | Lọc/biến đổi/gom nhóm dữ liệu | Gọi terminal operation 2 lần → `IllegalStateException`; dùng `parallelStream()` cho I/O-bound | "Stream dùng 1 lần, lazy tới khi có terminal op" |
| **Lambda** | Cách viết ngắn cho interface có đúng 1 abstract method | Callback/Predicate/Comparator ngắn | Logic dài vẫn nhồi vào lambda → khó đọc, khó test | "Lambda cho logic ngắn, method riêng cho logic dài" |
| **Lock (tổng quan)** | Cơ chế đảm bảo chỉ 1 thread vào critical section | Nhiều thread đọc/ghi state chung | Lock cả khối lớn không cần thiết → giảm throughput | "Ưu tiên: tránh share state > class thread-safe có sẵn > synchronized > ReentrantLock" |
| **volatile** | Đảm bảo visibility, KHÔNG đảm bảo atomicity | 1 writer nhiều reader, cờ trạng thái | Dùng cho `count++` (compound) → vẫn race condition | "volatile fix visibility, không fix atomicity" |
| **synchronized vs ReentrantLock** | Lock ẩn (JVM tự lock/unlock) vs lock tay (lập trình viên kiểm soát) | synchronized: đơn giản; ReentrantLock: cần tryLock/fairness/Condition | Quên `unlock()` trong `finally` với ReentrantLock → deadlock vĩnh viễn | "synchronized cho logic đơn giản; ReentrantLock khi cần tryLock/timeout để không treo client" |
| **Virtual Thread pinning (BONUS)** | VT bị ghim vào carrier khi block trong `synchronized` (trước Java 24) | Biết để giải thích vì sao team từng đổi sang ReentrantLock | Tưởng vẫn còn vấn đề này ở Java 24+ | "JEP 491 (Java 24) đã fix, synchronized không còn ghim VT nữa" |
| **CompletableFuture** | Future nâng cấp – non-blocking, chain/combine được, xử lý lỗi tốt | Gọi nhiều service độc lập song song rồi gộp | Dùng `ForkJoinPool.commonPool()` mặc định cho blocking I/O | "Luôn truyền Executor riêng khi gọi I/O trong CompletableFuture" |
| **JDK 8/11/17/21/24-25** | 8: Lambda/Stream; 11: var/HttpClient; 17: Record/Sealed; 21: Virtual Thread; 24-25: fix pinning + Structured Concurrency | Trả lời "đã dùng feature gì của bản nào" khi phỏng vấn | Nhớ lẫn bản nào ra feature nào | Thuộc đúng 1 feature đại diện/bản là đủ |
| **DDD** | Thiết kế xoay quanh nghiệp vụ, không xoay quanh DB/framework | Domain phức tạp, nhiều rule/trạng thái | Anemic Model (Entity chỉ có getter/setter, logic nằm hết ở Service) | "Rich Domain Model: business logic nằm trong Entity, không trong Service" |
| **DB & Distributed Lock (BONUS)** | Lock ngoài JVM: optimistic (version check) / pessimistic (SELECT FOR UPDATE) / distributed (Redis) | Service scale nhiều instance, cần idempotent | Lock trong JVM (synchronized) không bảo vệ được khi có nhiều pod | "Pessimistic lock khi tranh chấp cao + dữ liệu quan trọng; Redis lock khi cần chặn nhanh xuyên instance" |

---

*Tài liệu này được tổng hợp tham khảo Oracle Docs, Baeldung, Martin Fowler và các nguồn uy tín khác trong ngành Java vào tháng 6/2026. Java vẫn đang phát triển (Java 25 LTS đã ra mắt 9/2025) — nên định kỳ kiểm tra lại các link tài liệu để cập nhật phiên bản mới nhất. Bản cập nhật này bổ sung 3 phần BONUS (không nằm trong 12 keyword gốc, nhưng liên kết trực tiếp tới chúng): JEP 491/Virtual Thread pinning (mục 3, 9, 12), Lock ở tầng Database & Distributed Lock (mục 7b), và Cheat Sheet tổng hợp (mục 16).*
