# Workbook Thực Hành: 12 Keyword Java BE (Spring Boot)

> File này là **sổ tay luyện tập** — đi kèm với file tra cứu sâu `java-core-deep-dive.md` (đã gửi trước). Khi nào bí ở bước nào, quay lại đúng mục số trong file deep-dive để đọc phần đó, không đọc cả bài.
>
> **Bản cập nhật:** ngoài 12 keyword gốc (A1-A8, B1-B4), có thêm **B5 (BONUS)** về Lock ở tầng Database & Distributed Lock, vài note BONUS nhỏ về Virtual Thread pinning (JEP 491) gắn trong A6, và phần "Quy tắc ôn tập chống quên" ở cuối file — tất cả đều không bắt buộc nhưng giúp bạn trả lời tốt hơn các câu hỏi Senior thực tế.

## Cách dùng file này (đọc 1 lần, dùng lại 12 lần)

Mỗi keyword nhóm **Thực hành** đi theo đúng 3 bước, không đổi:
**Đập** (viết code lỗi/thiếu cơ chế, chạy thấy sai) → **Tò mò** (đặt 1 câu hỏi cụ thể, tra đúng phần đó) → **Vá** (sửa code, chạy đúng, đúc kết 1 câu).

Mỗi keyword nhóm **Lý thuyết (No-Code)** đi theo 3 bước khác:
**Câu hỏi mở đầu** → **Tìm câu trả lời** (đọc có mục tiêu) → **Liên hệ thực tế** (gắn vào ví dụ có thật).

**Quy tắc duy nhất cần nhớ: 1 keyword/buổi, tick đủ 4 checkbox mới qua keyword tiếp theo.** Không tick được hết trong thời gian ước lượng cũng không sao — dừng đúng giờ, hôm sau làm tiếp, đừng dồn.

---

## Checklist tổng tiến độ (tick dần khi xong)

### Nhóm A — Thực hành (có code, "đập" được) — dễ → khó

- [ ] A1. Lambda ⭐
- [ ] A2. Stream API ⭐⭐
- [ ] A3. Thread cơ bản ⭐⭐
- [ ] A4. volatile ⭐⭐
- [ ] A5. synchronized ⭐⭐⭐
- [ ] A6. Lock / ReentrantLock ⭐⭐⭐
- [ ] A7. ThreadLocal ⭐⭐⭐
- [ ] A8. CompletableFuture ⭐⭐⭐⭐

### Nhóm B — Lý thuyết, No-Code — dễ → khó

- [ ] B1. Concurrency vs Parallelism ⭐
- [ ] B2. Các phiên bản JDK (8/11/17/21/24) ⭐
- [ ] B3. Java Memory Model & happens-before ⭐⭐
- [ ] B4. DDD ⭐⭐⭐
- [ ] B5. (BONUS) Lock ở tầng Database & Distributed Lock ⭐⭐

---

# PHẦN A — NHÓM THỰC HÀNH (dễ → khó)

## A1. Lambda ⭐ (~40 phút)

Lambda = cách viết ngắn cho 1 method duy nhất của 1 interface.

- [ ] **Đập (10p):** Viết `Comparator` kiểu cũ (anonymous class) để sort list `Order` theo `amount` giảm dần. Code dài, lặp cú pháp — đó chính là "vấn đề" cần thấy.
```java
List<Order> orders = ...;
orders.sort(new Comparator<Order>() {
    @Override
    public int compare(Order o1, Order o2) {
        return Double.compare(o2.getAmount(), o1.getAmount());
    }
});
```
- [ ] **Tò mò (10p):** Tự hỏi "interface chỉ có 1 method thì viết gọn được không?" → tra mục 6 trong deep-dive, đọc đúng đoạn Functional Interface.
- [ ] **Vá (15p):** Viết lại bằng lambda, rồi viết lại lần nữa bằng method reference (`Comparator.comparingDouble(...).reversed()`). So sánh 3 cách.
- [ ] **Đúc kết (5p):** Tự viết 1 câu: "Lambda dùng khi ___, vì ___."

**Done khi:** bạn viết được lambda lọc 1 `Predicate<Order>` mà không nhìn code mẫu.

---

## A2. Stream API ⭐⭐ (~50 phút)

Stream = pipeline xử lý collection kiểu khai báo (filter → map → collect).

- [ ] **Đập (10p):** Viết bằng for-loop thường: lọc `Order` có `amount > 1_000_000`, lấy ra list email khách hàng (không trùng).
- [ ] **Tò mò (10p):** "Có cách viết không cần biến tạm `result`, không cần vòng `for` không?" → tra mục 5.
- [ ] **Vá (20p):** Viết lại bằng `stream().filter().map().distinct().collect()`. Sau đó tự thêm bài tập: nhóm `Order` theo `OrderStatus` bằng `groupingBy`.
- [ ] **Đúc kết (5p):** "Stream dùng khi ___, không nên dùng khi ___."

**Done khi:** bạn tự viết được 1 pipeline filter+map+collect mới (không phải ví dụ cũ) cho dữ liệu khác.

---

## A3. Thread cơ bản ⭐⭐ (~50 phút)

Thread = đơn vị thực thi độc lập; nhiều thread cùng chạy có thể đụng dữ liệu chung.

- [ ] **Đập (15p):** Viết 2 thread cùng tăng 1 biến `count` (không khoá gì) 100,000 lần mỗi thread, in ra kết quả cuối.
```java
class Counter { int count = 0; void increment() { count++; } }

Counter counter = new Counter();
Runnable task = () -> { for (int i = 0; i < 100_000; i++) counter.increment(); };
Thread t1 = new Thread(task);
Thread t2 = new Thread(task);
t1.start(); t2.start();
t1.join(); t2.join();
System.out.println(counter.count); // chạy vài lần, xem có ra 200000 không
```
- [ ] **Tò mò (10p):** Chạy 3-4 lần, ghi lại các số khác nhau ra. Tự hỏi "vì sao 2 thread cùng làm 1 việc giống nhau mà kết quả không cố định?" → tra mục 2, mục 3.
- [ ] **Vá (20p):** Chưa cần sửa đúng hẳn (việc đó là bài A5/A6) — chỉ cần: đổi `Runnable` thành `ExecutorService` với `newFixedThreadPool(2)` thay cho `new Thread()` tay, quan sát hành vi.

> **Lưu ý độ tin cậy:** nếu chạy vài lần vẫn ra đúng `200000` (không thấy bug), đừng vội kết luận code đúng — race condition phụ thuộc timing nên có thể "may" không lộ ra. Cách tái hiện chắc hơn: tăng số thread lên 8-10 và dùng `CountDownLatch` để ép tất cả thread bắt đầu **đúng cùng lúc** (thay vì rời rạc do `start()` gọi tuần tự):
```java
int threadCount = 10;
CountDownLatch startLatch = new CountDownLatch(1); // tín hiệu "bắt đầu" chung
CountDownLatch doneLatch = new CountDownLatch(threadCount);
for (int i = 0; i < threadCount; i++) {
    new Thread(() -> {
        try {
            startLatch.await();              // mọi thread đứng chờ ở đây
            for (int j = 0; j < 100_000; j++) counter.increment();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            doneLatch.countDown();
        }
    }).start();
}
startLatch.countDown();   // "bắn cò" cho tất cả thread chạy cùng lúc
doneLatch.await();        // chờ tất cả xong rồi mới in kết quả
System.out.println(counter.count);
```
Đây cũng chính là kỹ thuật dùng khi viết **unit test cho code concurrent** trong dự án thật (đừng test concurrency bằng cách chạy `main()` vài lần rồi nhìn bằng mắt).
- [ ] **Đúc kết (5p):** "Vấn đề tên là ___ (race condition), nguyên nhân là ___."

**Done khi:** bạn giải thích được bằng lời (không nhìn tài liệu) vì sao kết quả sai lệch.

---

## A4. volatile ⭐⭐ (~45 phút)

volatile = đảm bảo thread khác *thấy* giá trị mới ngay, không đảm bảo phép toán an toàn.

- [ ] **Đập (10p):** Viết 1 thread chạy `while (running) {}`, thread chính sau 100ms gọi `stop()` set `running = false`. Không khai báo `volatile`.
```java
class Flag {
    private boolean running = true;
    void run() { new Thread(() -> { while (running) {} System.out.println("stopped"); }).start(); }
    void stop() { running = false; }
}
```
Chạy nhiều lần — có khi nó **không bao giờ in "stopped"**.
- [ ] **Tò mò (10p):** "Sao thread kia không thấy biến đã đổi?" → tra mục 8, đọc đúng đoạn visibility/main memory.
- [ ] **Vá (15p):** Thêm `volatile` vào `running`, chạy lại — luôn in ra "stopped". Thử thêm: đổi `running` thành `int count` rồi cho 2 thread cùng `count++` dù đã `volatile` → vẫn sai (vì atomicity khác visibility).
- [ ] **Đúc kết (5p):** "volatile đảm bảo ___ nhưng KHÔNG đảm bảo ___."

**Done khi:** bạn tự giải thích được khác biệt visibility vs atomicity bằng ví dụ của chính bạn.

---

## A5. synchronized ⭐⭐⭐ (~50 phút)

synchronized = khoá độc quyền built-in, JVM tự lock/unlock.

- [ ] **Đập:** Quay lại bài A3 (2 thread cùng `count++`) — vẫn còn sai.
- [ ] **Tò mò (10p):** "volatile (A4) không fix được increment, vậy cái gì fix được?" → tra mục 9.
- [ ] **Vá (25p):** Thêm `synchronized` vào method `increment()`, chạy lại nhiều lần — luôn ra đúng `200000`. Thử thêm: đổi thành `synchronized` block trên 1 object lock riêng thay vì lock cả method, so sánh.
```java
class Counter {
    private int count = 0;
    public synchronized void increment() { count++; }
}
```
- [ ] **Đúc kết (5p):** "synchronized fix được vì ___ (so với volatile)."

**Done khi:** bài A3 chạy ra đúng 200000 mọi lần, bạn giải thích được tại sao volatile không đủ mà synchronized đủ.

---

## A6. Lock / ReentrantLock ⭐⭐⭐ (~55 phút)

ReentrantLock = lock thủ công, linh hoạt hơn synchronized (tryLock, timeout, fairness).

- [ ] **Đập (10p):** Giữ nguyên bài A5 đã chạy đúng — nhưng giờ giả lập tình huống: lock đang bị giữ lâu (thêm `Thread.sleep(2000)` trong synchronized block), và bạn muốn thread khác **không bị treo vô hạn** mà báo lỗi sau 500ms.
- [ ] **Tò mò (10p):** "synchronized có cách nào để 'thử lock rồi bỏ qua nếu không được' không?" → tra mục 9, đọc đúng đoạn `tryLock`.
- [ ] **Vá (30p):** Thay `synchronized` bằng `ReentrantLock` + `tryLock(500, TimeUnit.MILLISECONDS)`.
```java
private final Lock lock = new ReentrantLock();
public boolean increment() {
    try {
        if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
            try { count++; return true; } finally { lock.unlock(); }
        }
        return false; // không lấy được lock, không bị treo
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
    }
}
```
- [ ] **Đúc kết (5p):** Tự viết kịch bản của riêng bạn (không lấy ví dụ payment trong deep-dive) cho câu "khi nào tôi chọn ReentrantLock thay vì synchronized".

**Done khi:** bạn trả lời được câu hỏi bonus gốc — *"khác nhau giữa synchronized và ReentrantLock, khi nào dùng cái nào"* — bằng ví dụ TỰ BẠN code ra, không phải ví dụ trong tài liệu.

> **Đọc thêm (không cần code, 5p):** Có 1 lý do thực tế khác (ngoài tryLock/fairness) khiến nhiều team từng chọn `ReentrantLock` thay `synchronized`: vấn đề "Virtual Thread bị pinning" trước Java 24. Đọc phần BONUS ở mục 9 trong deep-dive để biết — không cần code lại, chỉ cần hiểu để trả lời được nếu bị hỏi.

---

## A7. ThreadLocal ⭐⭐⭐ (~50 phút)

ThreadLocal = mỗi thread giữ 1 bản riêng của biến, không đụng nhau.

- [ ] **Đập (15p):** Viết 1 `ExecutorService` pool 2 thread. Task 1 set 1 biến static `currentUser = "A"`, task 2 set `currentUser = "B"`, mỗi task sleep rồi in `currentUser` ra — quan sát task 1 có khi in ra "B" (do biến static bị chia sẻ).
- [ ] **Tò mò (10p):** "Làm sao để mỗi thread có bản riêng của `currentUser`, không bị ghi đè?" → tra mục 4.
- [ ] **Vá (20p):** Đổi biến static thành `ThreadLocal<String>`, dùng `.set()`/`.get()`. Chạy lại, mỗi task in đúng giá trị của mình. Thêm: thử bỏ `.remove()` ở cuối, rồi submit thêm task 3 vào cùng pool — quan sát xem có "thấy" giá trị cũ của task đã xong trước đó không (minh hoạ rủi ro leak với thread pool).
- [ ] **Đúc kết (5p):** "Phải gọi `.remove()` vì ___."

**Done khi:** bạn tái hiện được hiện tượng leak khi quên `remove()` và giải thích đúng nguyên nhân (thread pool tái sử dụng thread).

---

## A8. CompletableFuture ⭐⭐⭐⭐ (~60 phút)

CompletableFuture = chạy nhiều việc bất đồng bộ, gộp kết quả, không cần `.get()` block ngay.

- [ ] **Đập (10p):** Viết 3 method giả lập gọi API chậm (`Thread.sleep(1000)` rồi trả giá trị). Gọi tuần tự cả 3, đo tổng thời gian (sẽ ~3000ms).
- [ ] **Tò mò (15p):** "Làm sao 3 việc độc lập chạy cùng lúc để tổng thời gian chỉ ~1000ms?" → tra mục 11.
- [ ] **Vá (30p):** Viết lại bằng `CompletableFuture.supplyAsync()` cho cả 3, dùng `CompletableFuture.allOf()` để chờ tất cả, đo lại thời gian.
```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> slowCall("A"));
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> slowCall("B"));
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> slowCall("C"));
CompletableFuture.allOf(f1, f2, f3).join();
System.out.println(f1.join() + f2.join() + f3.join());
```
- [ ] **Đúc kết (5p):** "Tổng thời gian giảm từ ___ xuống ___, vì ___."

**Done khi:** bạn đo được thời gian thực tế giảm và giải thích đúng lý do (chạy song song thay vì tuần tự).

---

# PHẦN B — NHÓM LÝ THUYẾT, NO-CODE (dễ → khó)

> Nhóm này không có gì để "đập vỡ" bằng code — thay vào đó, mỗi mục bắt đầu bằng 1 câu hỏi cụ thể (để tạo sự tò mò giống nhóm A), rồi đọc có mục tiêu, rồi gắn vào ví dụ thật.

## B1. Concurrency vs Parallelism ⭐ (~25 phút)

- [ ] **Câu hỏi mở đầu (5p):** "Máy tính tôi chỉ có 1 core thì có thể chạy 'song song' (parallelism) thật không?"
- [ ] **Tìm câu trả lời (15p):** Tra mục 2 trong deep-dive, ghi ra 3 bullet trả lời đúng câu hỏi trên + định nghĩa 2 khái niệm.
- [ ] **Liên hệ thực tế (5p):** Trong project bạn từng làm (hoặc bài A8 vừa làm), việc gọi 3 API song song bằng `CompletableFuture` là concurrency hay parallelism? Tự trả lời, không cần đúng tuyệt đối, quan trọng là tự lý giải được.

**Done khi:** bạn phân biệt được 2 khái niệm bằng ví dụ của chính bạn, không lặp lại ví dụ đầu bếp trong tài liệu.

---

## B2. Các phiên bản JDK: 8, 11, 17, 21 ⭐ (~25 phút)

- [ ] **Câu hỏi mở đầu (5p):** "Nếu tôi chỉ được nhớ 1 feature quan trọng nhất của mỗi bản Java 8/11/17/21, tôi sẽ chọn cái nào?"
- [ ] **Tìm câu trả lời (15p):** Tra mục 12, đọc bảng tổng hợp, tự chọn 1 feature/bản (không cần học hết bảng).
- [ ] **Liên hệ thực tế (5p):** Feature bạn vừa code ở A1-A8 (Lambda, Stream, CompletableFuture) thuộc bản Java nào? Virtual Thread (Java 21) khác `ExecutorService` bạn dùng ở A3 như thế nào — tự đoán trước khi tra lại.

**Done khi:** bạn nói được "tôi đang dùng/biết tới Java bản mấy, vì sao" mà không cần học thuộc cả bảng.

---

## B3. Java Memory Model & happens-before ⭐⭐ (~30 phút)

- [ ] **Câu hỏi mở đầu (5p):** Quay lại bài A4 (volatile) — "tại sao thread A ghi 1 biến, thread B đọc, mà B vẫn có thể thấy giá trị CŨ nếu không có volatile/synchronized?"
- [ ] **Tìm câu trả lời (20p):** Tra mục 10, ghi ra 3 nguồn tạo "happens-before" (lock/unlock, volatile, `thread.join()`).
- [ ] **Liên hệ thực tế (5p):** Trong bài A4 và A5 bạn vừa làm, hành động nào chính là "happens-before" giúp fix được bug?

**Done khi:** bạn giải thích lại được bug ở bài A4 bằng đúng thuật ngữ "happens-before" mà không cần nhìn định nghĩa.

---

## B4. DDD (Domain-Driven Design) ⭐⭐⭐ (~40 phút)

- [ ] **Câu hỏi mở đầu (5p):** "`Order` (đơn hàng) trong hệ thống có id riêng, còn `Money` (số tiền) thì không cần id để so sánh bằng nhau — 2 khái niệm này khác nhau ở điểm gì, gọi tên là gì?"
- [ ] **Tìm câu trả lời (20p):** Tra mục 13, ghi ra định nghĩa Entity, Value Object, Aggregate Root, Repository bằng lời bạn (không copy).
- [ ] **Liên hệ thực tế (15p):** Lấy chính project "đặt vé xem phim" (hoặc project bạn đang học) — tự xác định: cái gì là Entity (có id), cái gì là Value Object, cái gì nên là Aggregate Root.

**Done khi:** bạn vẽ được (trên giấy/note) sơ đồ Entity – Value Object – Aggregate Root cho đúng project của bạn, không phải project ví dụ Order trong tài liệu.

---

## B5. (BONUS) Lock ở tầng Database & Distributed Lock ⭐⭐ (~30 phút)

> Mục này không nằm trong 12 keyword gốc của bạn, nhưng nên làm vì nó trả lời trực tiếp câu hỏi Senior "thiết kế thanh toán idempotent, lock ở tầng nào" mà bạn sẽ gặp khi phỏng vấn BE.

- [ ] **Câu hỏi mở đầu (5p):** "Tôi đã biết `synchronized`/`ReentrantLock` chống race condition trong 1 JVM. Nếu service của tôi chạy 2-3 instance song song (scale ngang), 2 request trùng lặp rơi vào 2 instance khác nhau — `synchronized` còn bảo vệ được không?"
- [ ] **Tìm câu trả lời (15p):** Tra mục 7b trong deep-dive, ghi ra bằng lời bạn: Optimistic Lock khác Pessimistic Lock khác Distributed Lock ở điểm nào, mỗi loại tốn chi phí gì.
- [ ] **Liên hệ thực tế (10p):** Lấy lại bài A6 (PaymentService dùng `ReentrantLock` + `tryLock`) — nếu `PaymentService` này được deploy ra 3 instance, `ReentrantLock` trong A6 có còn đủ không? Bạn sẽ đổi gì (gợi ý: chuyển sang Redis distributed lock hoặc pessimistic lock ở DB cho đúng order đó)?

**Done khi:** bạn giải thích được vì sao lock trong A5/A6 (trong 1 JVM) là chưa đủ khi hệ thống scale ra nhiều instance, và chọn được loại lock phù hợp cho ví dụ thanh toán của riêng bạn.

---

# Lộ trình gợi ý theo ngày (full-time 4h+/ngày, ghép 2 nhóm hợp lý)

Không nhất thiết làm hết nhóm A rồi mới sang nhóm B — một số bài lý thuyết "khớp" tốt hơn nếu đặt ngay sau bài thực hành liên quan (ví dụ B3 - JMM nên làm ngay sau A4 - volatile, lúc câu hỏi còn "nóng").

| Ngày | Buổi sáng (~2-2.5h) | Buổi chiều (~2-2.5h) |
|---|---|---|
| 1 | A1. Lambda | A2. Stream API |
| 2 | B1. Concurrency vs Parallelism | A3. Thread cơ bản |
| 3 | A4. volatile | B3. JMM & happens-before (làm ngay sau, câu hỏi còn nóng) |
| 4 | A5. synchronized | A6. ReentrantLock |
| 5 | A7. ThreadLocal | B5. (BONUS) DB & Distributed Lock (làm ngay sau A6, câu hỏi lock còn nóng) |
| 6 | B2. Các phiên bản JDK | A8. CompletableFuture (bài nặng nhất, để buổi sáng đỡ mệt hơn buổi chiều) |
| 7 | **Nghỉ hoàn toàn** | **Nghỉ hoàn toàn** |
| 8 | B4. DDD (lý thuyết) | Bắt đầu refactor project "đặt vé xem phim" áp DDD vào |
| 9 | Hoàn thiện refactor DDD | Tự giải thích lại miệng toàn bộ keyword A1→A8, B1→B5 — không nhìn tài liệu |
| 10 | Mock interview tổng (tự hỏi-tự trả lời hoặc nhờ người khác/Claude hỏi từ mục 14 deep-dive) | Ôn Cheat Sheet (mục 16 deep-dive), khoanh vùng 2-3 chỗ còn yếu nhất để ôn thêm trước khi đi phỏng vấn thật |

→ Hết ngày 10 là xong cả 12 keyword + 2 phần BONUS theo đúng vòng lặp, có project chạy thật, và đã tự kiểm tra lại bằng mock interview — không phải chỉ đọc xong.

---

# Quy tắc chống nản (nhắc lại, dán ở đầu màn hình nếu cần)

1. Không tick được đủ 4 checkbox trong giờ ước lượng → dừng đúng giờ, không học bù, hôm sau tiếp tục đúng chỗ dừng.
2. Đọc lại tài liệu deep-dive 1 mục mà vẫn rối → đó là dấu hiệu cần dừng đọc, không phải đọc thêm lần nữa.
3. Ngày 7 nghỉ thật, không "lỡ" mở code — não cần thời gian này để kiến thức "ngấm".
4. Mỗi khi xong 1 keyword, quay lại tick ô checkbox trong "Checklist tổng tiến độ" ở đầu file — nhìn checklist đầy dần lên là động lực thật, không phải động lực ảo.

---

# Quy tắc ôn tập chống quên (Spaced Repetition) — không thêm tải, chỉ thêm 5-10 phút

Hiểu đúng lúc học không đồng nghĩa với nhớ được tới lúc phỏng vấn thật (thường vài tuần sau). Hai thói quen nhỏ sau **không cần thêm thời gian học mới**, chỉ cần chèn vào lịch đã có:

1. **Ôn 5 phút mỗi 3 ngày:** trước khi bắt đầu buổi học mới, mở checklist, chọn 1 keyword đã tick ở 2-3 ngày trước, tự nói ra miệng trong 2 phút (không nhìn tài liệu): định nghĩa là gì, khi nào dùng, 1 lỗi hay gặp. Nói được trôi chảy → bỏ qua, học bài mới. Nói vấp → đọc lại đúng phần đó trong deep-dive (không đọc lại cả mục).
2. **Luyện nói, không chỉ luyện viết:** viết được câu trả lời (như các bài "Đúc kết" ở trên) và **nói lưu loát trong phỏng vấn thật** là 2 kỹ năng khác nhau. Vào ngày 10 (mock interview), đừng đọc lại câu trả lời đã viết — hãy tự hỏi to 1 câu trong mục 14 (deep-dive) rồi trả lời bằng miệng, ghi âm lại và nghe thử. Nếu có người quen học cùng hoặc dùng Claude, có thể nhờ đóng vai người phỏng vấn hỏi ngẫu nhiên từ bộ câu hỏi đó để tập phản xạ trả lời không chuẩn bị trước.

Mục tiêu của 2 thói quen này: tới ngày thật sự đi phỏng vấn (có thể vài tuần sau khi xong lộ trình), bạn không phải học lại từ đầu — chỉ cần mở mục 16 (Cheat Sheet) ôn nhanh 15 phút là đủ tự tin.

