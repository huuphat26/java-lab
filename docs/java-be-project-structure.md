# Cây Thư Mục Project: Java BE Roadmap (Code-along cho 12 Keyword + Bonus)

> File này là tài liệu thứ 3, đi kèm `java-core-deep-dive.md` (tra cứu) và `java-be-workbook.md` (sổ tay luyện tập). Mục tiêu: biến repo GitHub của bạn thành nơi **vừa học vừa chạy được code thật**, vì đọc tài liệu chỉ chiếm 30% — phần còn lại nằm ở việc tự tay viết, chạy, thấy lỗi, sửa lỗi.

---

## 0. Tư duy tổ chức (đọc trước khi tạo folder)

12 keyword gốc của bạn chia làm 2 nhóm rất khác nhau về môi trường chạy:

| Nhóm | Đặc điểm | Cần Spring Boot không? |
|---|---|---|
| Lambda, Stream, Thread, volatile, synchronized, ReentrantLock, ThreadLocal, CompletableFuture, JMM, Concurrency/Parallelism | Java thuần, chạy bằng 1 `main()`, không cần server | **Không** |
| DDD (B4), Lock DB & Distributed Lock (B5 bonus) | Cần Entity/Repository/Controller thật, cần DB/Redis | **Có** |

→ Vì vậy repo nên tách **2 module** rõ ràng ngay từ đầu, thay vì nhồi hết vào 1 project Spring Boot (sẽ làm các bài Thread/volatile/Lock đơn giản bị "nặng" không cần thiết, chạy chậm vì phải khởi động cả Spring context chỉ để test `count++`).

Lợi ích phụ: một repo có cấu trúc multi-module rõ ràng, tách đúng theo layer DDD, **chính nó cũng là 1 bằng chứng kỹ năng** khi nhà tuyển dụng xem GitHub của bạn — không chỉ là nơi lưu code học tập.

---

## 1. Cây thư mục tổng (đây là root repo bạn sẽ `git init` và up GitHub)

```
java-be-roadmap/
│
├── README.md                         ← Trang giới thiệu repo (xem mục 6)
├── .gitignore                        ← Bỏ qua target/, .idea/, *.class...
├── pom.xml                           ← Root POM, packaging "pom", khai báo 2 module bên dưới
│
├── docs/                             ← 2 file bạn đã có, copy nguyên vào đây
│   ├── java-core-deep-dive.md
│   ├── java-be-workbook.md
│   └── java-be-project-structure.md  ← chính file này
│
├── java-core-lab/                    ← MODULE 1: Java thuần, không Spring
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/<tenban>/corelab/
│       │   ├── a1_lambda/
│       │   │   ├── BeforeLambda.java        (bước "Đập" — anonymous class)
│       │   │   └── AfterLambda.java         (bước "Vá" — lambda + method reference)
│       │   ├── a2_streamapi/
│       │   │   ├── BeforeStream.java        (for-loop thường)
│       │   │   └── AfterStream.java         (filter/map/collect/groupingBy)
│       │   ├── a3_thread/
│       │   │   ├── RaceConditionDemo.java   (2 thread cùng count++, chưa fix)
│       │   │   └── FixedWithLatch.java      (bản dùng CountDownLatch ép chạy cùng lúc)
│       │   ├── a4_volatile/
│       │   │   ├── WithoutVolatile.java     (while(running){} không bao giờ dừng)
│       │   │   └── WithVolatile.java
│       │   ├── a5_synchronized/
│       │   │   └── SynchronizedCounter.java
│       │   ├── a6_reentrantlock/
│       │   │   ├── TryLockPaymentDemo.java  (kịch bản tryLock 500ms)
│       │   │   └── BoundedBufferDemo.java   (Condition, producer/consumer)
│       │   ├── a7_threadlocal/
│       │   │   ├── StaticLeakDemo.java      (biến static bị đụng giữa 2 thread)
│       │   │   └── ThreadLocalFixed.java
│       │   ├── a8_completablefuture/
│       │   │   ├── SequentialCallsDemo.java (gọi 3 API tuần tự, ~3000ms)
│       │   │   └── ParallelWithCF.java      (dùng allOf, ~1000ms)
│       │   ├── b1_concurrency_vs_parallelism/
│       │   │   └── ParallelStreamDemo.java
│       │   ├── b3_jmm_happens_before/
│       │   │   └── HappensBeforeNotes.java  (code minh hoạ, chủ yếu là comment giải thích)
│       │   └── bonus_jep491_pinning/
│       │       └── VirtualThreadPinningDemo.java  (so sánh hành vi JDK <24 vs JDK 24+)
│       │
│       └── test/java/com/<tenban>/corelab/
│           ├── a3_thread/CounterConcurrencyTest.java     ← JUnit + CountDownLatch
│           ├── a5_synchronized/SynchronizedCounterTest.java
│           ├── a6_reentrantlock/TryLockPaymentTest.java
│           └── a8_completablefuture/ParallelWithCFTest.java
│
└── booking-spring-app/               ← MODULE 2: Spring Boot — áp DDD + Lock DB/Redis
    ├── pom.xml
    └── src/
        ├── main/java/com/<tenban>/booking/
        │   ├── BookingApplication.java
        │   │
        │   ├── domain/                       ← mục 13 deep-dive: KHÔNG phụ thuộc Spring/JPA
        │   │   ├── seat/
        │   │   │   ├── Seat.java             (Entity)
        │   │   │   └── SeatStatus.java
        │   │   ├── booking/
        │   │   │   ├── Booking.java          (Aggregate Root)
        │   │   │   ├── BookingId.java        (Value Object)
        │   │   │   └── BookingRepository.java (interface, định nghĩa ở domain)
        │   │   └── showtime/
        │   │       └── Showtime.java
        │   │
        │   ├── application/                  ← Use case, điều phối domain
        │   │   └── BookSeatUseCase.java
        │   │
        │   ├── infrastructure/                ← chi tiết kỹ thuật, implement interface domain
        │   │   ├── persistence/
        │   │   │   ├── JpaBookingRepository.java
        │   │   │   └── BookingJpaEntity.java
        │   │   └── lock/
        │   │       ├── PessimisticSeatLock.java     (bonus B5 — SELECT FOR UPDATE)
        │   │       └── RedisDistributedLock.java    (bonus B5 — Redisson)
        │   │
        │   └── presentation/                  ← Controller, DTO
        │       ├── BookingController.java
        │       └── dto/
        │           ├── BookSeatRequest.java
        │           └── BookSeatResponse.java
        │
        ├── main/resources/
        │   └── application.yml
        │
        └── test/java/com/<tenban>/booking/
            ├── domain/booking/BookingTest.java
            └── application/BookSeatUseCaseTest.java
```

---

## 2. Bảng tra cứu nhanh: keyword → folder → tài liệu liên quan

| Keyword (workbook) | Folder trong repo | Mục tương ứng trong deep-dive |
|---|---|---|
| A1. Lambda | `java-core-lab/.../a1_lambda` | Mục 6 |
| A2. Stream API | `java-core-lab/.../a2_streamapi` | Mục 5 |
| A3. Thread cơ bản | `java-core-lab/.../a3_thread` | Mục 2, 3 |
| A4. volatile | `java-core-lab/.../a4_volatile` | Mục 8 |
| A5. synchronized | `java-core-lab/.../a5_synchronized` | Mục 9 |
| A6. ReentrantLock | `java-core-lab/.../a6_reentrantlock` | Mục 7, 9 |
| A7. ThreadLocal | `java-core-lab/.../a7_threadlocal` | Mục 4 |
| A8. CompletableFuture | `java-core-lab/.../a8_completablefuture` | Mục 11 |
| B1. Concurrency vs Parallelism | `java-core-lab/.../b1_concurrency_vs_parallelism` | Mục 2 |
| B2. JDK versions | *(không cần code riêng — đọc bảng mục 12)* | Mục 12 |
| B3. JMM & happens-before | `java-core-lab/.../b3_jmm_happens_before` | Mục 10 |
| B4. DDD | `booking-spring-app/.../domain`, `application` | Mục 13 |
| B5. (BONUS) DB & Distributed Lock | `booking-spring-app/.../infrastructure/lock` | Mục 7b |
| BONUS. Virtual Thread pinning | `java-core-lab/.../bonus_jep491_pinning` | Mục 3, 9 (phần BONUS) |

Mỗi lần bí ở 1 bài trong workbook, bạn đã biết ngay: mở folder nào trong IntelliJ, đọc mục nào trong deep-dive.

---

## 3. Dựng project này trong IntelliJ IDEA (không cần gõ code, chỉ cần tạo đúng khung)

1. **Tạo root project:** `File → New → Project` → chọn **Maven** (không chọn template Spring ở bước này) → đặt tên `java-be-roadmap` → Java SDK chọn bản 21 hoặc 24/25 nếu máy bạn đã cài (để dùng được Virtual Thread cho bonus).
2. **Đổi `pom.xml` gốc thành packaging `pom`** và khai báo 2 module (`java-core-lab`, `booking-spring-app`) — đây là phần duy nhất cần sửa tay trong file cấu hình, không phải code logic.
3. **Tạo module 1:** Click phải vào root project → `New → Module` → Maven, đặt tên `java-core-lab`. IntelliJ tự tạo `src/main/java` và `src/test/java` bên trong — bạn chỉ cần tạo thêm các package theo đúng tên ở cây thư mục mục 1 (`a1_lambda`, `a2_streamapi`...).
4. **Tạo module 2:** Cách dễ nhất là dùng **Spring Initializr** ngay trong IntelliJ (`File → New → Module → Spring Initializr`), chọn dependency: `Spring Web`, `Spring Data JPA`, `Validation`, `Lombok` (tuỳ chọn), `H2 Database` (để chạy thử không cần cài DB thật). Đặt tên module `booking-spring-app`. Sau đó tạo các package con `domain/`, `application/`, `infrastructure/`, `presentation/` đúng cấu trúc DDD ở mục 1.
5. **Chạy code:**
   - Với `java-core-lab`: mỗi file `Before*.java`/`After*.java` có `main()` riêng — bấm nút ▶ (Run) cạnh tên class, không cần cấu hình gì thêm.
   - Với `booking-spring-app`: chạy `BookingApplication.java` (có `@SpringBootApplication`) để khởi động server, dùng Postman/`curl`/IntelliJ HTTP Client để gọi thử endpoint trong `presentation/`.
6. **Chạy test:** click phải vào folder `src/test/java` → `Run All Tests`, hoặc chạy từng file test (đúng pattern dùng `CountDownLatch` đã thêm trong workbook A3) để tự kiểm chứng race condition đã fix thật, không phải "chạy `main()` vài lần rồi nhìn bằng mắt".

---

## 4. Quy ước đặt tên file (để nhìn tên là biết đang ở bước nào)

- **`Before*.java`** = bước "Đập" trong workbook — code cố ý có lỗi/thiếu cơ chế, chạy để thấy sai.
- **`After*.java`** = bước "Vá" — code đã sửa đúng.
- Với bài có nhiều biến thể (ví dụ A6 có cả `tryLock` và `Condition`), tách thành 2 file riêng (`TryLockPaymentDemo.java`, `BoundedBufferDemo.java`) thay vì nhồi vào 1 file dài — vì mục tiêu là tra lại dễ, không phải code gọn nhất có thể.
- File test luôn đặt tên `<TênClassChính>Test.java` và nằm trong `src/test`, không nằm chung folder với code chính (chuẩn Maven/Gradle, IntelliJ tự nhận diện).

---

## 5. Gợi ý dùng Git để chính lịch sử commit cũng kể lại quá trình học

Vì repo này sẽ up GitHub, hãy commit theo từng bài trong workbook thay vì 1 commit khổng lồ cuối cùng — lịch sử commit sẽ tự trở thành "nhật ký học tập" mà nhà tuyển dụng có thể xem được:

```
git commit -m "A1: Lambda - viết lại Comparator bằng lambda + method reference"
git commit -m "A3: Thread - tái hiện race condition, fix bằng CountDownLatch trong test"
git commit -m "A6: ReentrantLock - tryLock 500ms cho PaymentService"
git commit -m "B4+B5: DDD - dựng Booking aggregate, thêm pessimistic lock cho Seat"
```

Có thể tạo 1 branch riêng `wip/keyword-dang-hoc` nếu muốn giữ `main` luôn sạch, nhưng với mục đích học, commit thẳng vào `main` theo đúng thứ tự ngày học (mục lộ trình 10 ngày trong workbook) cũng hoàn toàn ổn — đơn giản hơn, và lịch sử ngày-commit khớp với lộ trình càng dễ kể chuyện khi phỏng vấn hỏi "bạn học Java thế nào".

---

## 6. README.md gốc nên có gì (gợi ý nội dung, không phải code)

Khi mở repo này trên GitHub, người xem (hoặc bạn 6 tháng sau) nên thấy ngay ở README:
1. 1-2 câu mục tiêu repo: "Workspace luyện 12 keyword Java Core/Concurrency hướng Java BE, đi kèm tài liệu tra cứu trong `docs/`."
2. Link tới 2 file trong `docs/` (deep-dive, workbook).
3. Bảng ở mục 2 phía trên (keyword → folder) — copy thẳng vào README để không cần mở file này nữa.
4. 1 dòng hướng dẫn chạy: "Mở bằng IntelliJ IDEA, import như Maven multi-module project, chạy class `main()` tương ứng trong `java-core-lab` hoặc `BookingApplication` trong `booking-spring-app`."
5. Checklist tiến độ (có thể copy nguyên checklist ở đầu `java-be-workbook.md`, tick dần ngay trên GitHub — GitHub render được checkbox markdown).

---

*File này chỉ mô tả cấu trúc — không chứa code thực thi. Khi bắt đầu code theo từng bài trong `java-be-workbook.md`, tạo file đúng theo tên/vị trí ở mục 1, rồi quay lại tick checklist tương ứng.*
