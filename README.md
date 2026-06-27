# Java BE Roadmap 🚀

Workspace luyện **12 keyword Java Core/Concurrency** hướng Java BE, đi kèm tài liệu tra cứu trong `docs/`.

## 📚 Tài liệu

- [Java Core Deep Dive](docs/java-core-deep-dive.md) — Tra cứu chi tiết từng keyword
- [Java BE Workbook](docs/java-be-workbook.md) — Sổ tay luyện tập (Đập → Vá → Viết test)
- [Project Structure](docs/java-be-project-structure.md) — Giải thích cấu trúc thư mục

## 🏗️ Cấu trúc Project

Multi-module Maven project gồm 2 module:

| Module | Mô tả | Cần Spring? |
|---|---|---|
| `java-core-lab` | Java thuần — Lambda, Stream, Thread, volatile, synchronized... | ❌ Không |
| `booking-spring-app` | Spring Boot — DDD + Lock DB/Redis | ✅ Có |

## 🗂️ Keyword → Folder

| Keyword | Folder | Deep-dive |
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
| B3. JMM & happens-before | `java-core-lab/.../b3_jmm_happens_before` | Mục 10 |
| B4. DDD | `booking-spring-app/.../domain`, `application` | Mục 13 |
| B5. DB & Distributed Lock | `booking-spring-app/.../infrastructure/lock` | Mục 7b |
| BONUS. Virtual Thread | `java-core-lab/.../bonus_jep491_pinning` | Mục 3, 9 |

## 🚀 Hướng dẫn chạy

1. Mở bằng **IntelliJ IDEA**, import như **Maven multi-module project**
2. **Java Core Lab**: Chạy class `main()` tương ứng (click ▶ cạnh tên class)
3. **Booking App**: Chạy `BookingApplication.java` → dùng Postman/curl gọi API
4. **Test**: Click phải vào `src/test/java` → `Run All Tests`

```bash
# Hoặc chạy bằng Maven
mvn clean compile          # Compile tất cả
mvn test                   # Chạy tất cả test
mvn -pl java-core-lab test # Chạy test module 1
```

## ✅ Checklist tiến độ

- [ ] A1. Lambda & Functional Interface
- [ ] A2. Stream API
- [ ] A3. Thread cơ bản + Race condition
- [ ] A4. volatile
- [ ] A5. synchronized
- [ ] A6. ReentrantLock
- [ ] A7. ThreadLocal
- [ ] A8. CompletableFuture
- [ ] B1. Concurrency vs Parallelism
- [ ] B3. JMM & Happens-Before
- [ ] B4. DDD (Domain-Driven Design)
- [ ] B5. DB Lock & Distributed Lock
- [ ] BONUS. Virtual Thread Pinning (JEP 491)

---

*Commit theo từng bài trong workbook để lịch sử commit trở thành "nhật ký học tập".*
