# Bộ Bài Tập Bổ Sung: Thread — Đồng Thời (Concurrency) & Song Song (Parallelism)

> Đi kèm bài **A3. Thread cơ bản** trong workbook chính của bạn. Bài A3 trong workbook chỉ có 1 ví dụ (counter race condition) — file này thêm các bài tập khác để bạn "đập" nhiều tình huống hơn, hiểu sâu hơn trước khi qua A4 (volatile) và A5 (synchronized).
>
> **Lưu ý quan trọng:** ở giai đoạn này bạn **chưa học `synchronized`/`Lock`** — nên với nhóm bài về race condition, mục tiêu chỉ là **quan sát và giải thích đúng nguyên nhân**, KHÔNG cần sửa cho hết bug (việc sửa là bài A5/A6 sắp tới). Đừng cố tự nghĩ ra cách fix bây giờ, dễ làm sai hoặc bị rối.

---

## Nhóm 1 — Tính Đồng Thời (Concurrency): nhiều thread đụng dữ liệu chung

### Bài 1: Tài khoản ngân hàng bị "rút lụi" 💸

**Mục tiêu:** thấy race condition ở 1 bài toán thực tế hơn counter đơn thuần (đây chính là bug kinh điển trong hệ thống thanh toán).

**Đề bài:**
- Tạo 1 class `Account` có field `balance = 1_000_000`.
- Viết method `withdraw(int amount)`: kiểm tra `if (balance >= amount)` rồi `balance -= amount` (cố tình **không** khoá gì).
- Tạo 2 thread, mỗi thread gọi `withdraw(500)` đúng 1000 lần.
- Sau khi cả 2 thread chạy xong (`join()`), in `balance` ra và so sánh với kết quả đúng về lý thuyết: `1_000_000 - 2*1000*500 = 0`.

**Gợi ý:**
- Nếu muốn ép bug lộ ra chắc hơn (không phải "may mắn" ra đúng), thêm `Thread.sleep(0)` hoặc 1 vòng `for` rỗng ngắn giữa bước kiểm tra và bước trừ tiền trong `withdraw()` — việc này "giả lập" khoảng hở thời gian giữa check và update, đúng là nguyên nhân gốc của race condition.
- Thử in thêm: đếm số lần `balance` bị âm (lẽ ra không bao giờ được âm vì đã check `if`) — nếu bạn thấy `balance < 0` xuất hiện, đó là bằng chứng rõ nhất của race condition.

**Đúc kết:** Viết 1 câu giải thích vì sao `if (balance >= amount)` rồi `balance -= amount` là 2 bước **không atomic**, dù mỗi bước nhìn riêng lẻ có vẻ vô hại.

---

### Bài 2: Danh sách đơn hàng "biến mất" bí ẩn 📦

**Mục tiêu:** thấy race condition không chỉ xảy ra với số (`int`), mà cả với Collection.

**Đề bài:**
- Tạo 1 `ArrayList<Integer> orderIds = new ArrayList<>()`.
- 2 thread, mỗi thread `add()` 5000 số vào list đó (thread 1 add số 1-5000, thread 2 add số 5001-10000).
- Sau khi join xong, in `orderIds.size()` ra — kỳ vọng là 10000, kiểm tra thực tế có đúng không.

**Gợi ý:**
- Có thể bạn sẽ thấy size sai (ít hơn 10000), hoặc có lúc gặp `ArrayIndexOutOfBoundsException` văng ra ngay khi đang chạy — cả 2 hiện tượng đều hợp lệ, đều là hệ quả của `ArrayList` không thread-safe.
- Nếu chạy vài lần không thấy lỗi gì, đừng vội kết luận "ArrayList an toàn" — tăng số lượng phần tử lên (vd 50000/thread) để tăng khả năng 2 thread đụng nhau đúng lúc.

**Đúc kết:** Tự trả lời — vì sao 1 cấu trúc dữ liệu "bình thường chạy đúng" khi dùng 1 thread lại có thể lỗi khi dùng nhiều thread?

---

## Nhóm 2 — Tính Song Song (Parallelism): chia nhỏ việc để tăng tốc

> Khác nhóm 1: ở nhóm này **mỗi thread tự xử lý phần riêng của mình**, không đụng vào dữ liệu chung của thread khác khi đang tính — nên không có race condition, chỉ cần quan tâm tới **tốc độ**.

### Bài 3: Tính tổng mảng 20 triệu phần tử

**Đề bài:**
- Tạo `int[] data = new int[20_000_000]`, fill giá trị ngẫu nhiên (hoặc toàn số 1 cho dễ kiểm tra kết quả).
- **Cách 1:** dùng 1 thread/vòng lặp thường tính tổng toàn bộ mảng, đo thời gian bằng `System.nanoTime()`.
- **Cách 2:** chia mảng thành 4 phần bằng nhau, tạo 4 thread, mỗi thread tính tổng phần riêng của mình và lưu kết quả vào 1 ô riêng trong `long[] partialSums = new long[4]` (mỗi thread chỉ viết vào đúng 1 ô của mình — không đụng ô của thread khác).
- Sau khi 4 thread `join()` xong, cộng 4 giá trị trong `partialSums` lại, đo lại thời gian, so sánh với Cách 1.

**Gợi ý:**
- Tổng 2 cách phải ra **kết quả giống nhau tuyệt đối** (đây không phải bài về race condition, nên không được sai số).
- Nếu máy bạn ít core, thời gian Cách 2 có thể không nhanh hơn nhiều (hoặc thậm chí chậm hơn do overhead tạo thread) — đó cũng là 1 kết luận hợp lệ, hãy ghi lại số core máy bạn (`Runtime.getRuntime().availableProcessors()`) để giải thích.

**Đúc kết:** Đây là ví dụ "CPU-bound" — viết 1 câu giải thích vì sao chia việc ra nhiều thread giúp tận dụng nhiều core, khác với việc chia việc I/O-bound (sẽ gặp lại ở bài 4 và ở B1).

---

### Bài 4: Giả lập xử lý batch — Tuần tự vs Song song

**Đề bài:**
- Viết method `processFile(int fileId)`: in `"Đang xử lý file " + fileId`, `Thread.sleep(500)` (giả lập việc tốn thời gian như gọi API/đọc file), in `"Xong file " + fileId`.
- **Cách 1:** gọi `processFile()` tuần tự cho 10 file (id 1-10), đo tổng thời gian (kỳ vọng ~5000ms).
- **Cách 2:** tạo 10 thread, mỗi thread gọi `processFile()` cho 1 file, start tất cả, `join()` tất cả, đo tổng thời gian (kỳ vọng chỉ còn ~500-600ms vì chạy song song).

**Gợi ý:**
- Đây là ví dụ "I/O-bound" giả lập (giống việc gọi API chậm) — khác bài 3 (CPU-bound). Ghi nhớ sự khác biệt này để làm tốt bài B1 (Concurrency vs Parallelism).
- **Mở rộng (optional, làm sau khi xong các bài trên):** viết lại Cách 2 bằng `ExecutorService.newFixedThreadPool(10)` thay cho tạo `new Thread()` tay 10 lần — so sánh code gọn hơn ra sao. Đây chính là bước workbook A3 gợi ý ("đổi Runnable thành ExecutorService").

**Đúc kết:** Vì sao thời gian giảm gần 10 lần ở Cách 2, dù máy bạn có thể chỉ có 4-8 core (gợi ý: vì đây là I/O-bound, thread chủ yếu *chờ*, không chiếm CPU liên tục — liên hệ trực tiếp tới bài B1 bạn sắp học).

---

## Nhóm 3 — Điều phối Thread (lifecycle & coordination)

### Bài 5: `join()` để đảm bảo thứ tự đúng

**Đề bài:**
- Thread A: `Thread.sleep(1000)` rồi set `sharedData = "Dữ liệu đã sẵn sàng"`.
- Thread B: đọc `sharedData` và in ra ngay (không chờ gì).
- **Lần 1:** chạy mà không gọi `A.join()` trước khi B đọc — quan sát B in ra gì (thường là `null` vì A chưa kịp set).
- **Lần 2:** thêm `A.join()` trước khi B chạy (hoặc trước khi đọc `sharedData`) — quan sát B luôn in đúng giá trị.

**Đúc kết:** Giải thích `join()` đang làm gì ở đây — nó "ép" thread gọi nó phải đợi thread kia xong rồi mới chạy tiếp.

---

### Bài 6: Daemon Thread — tự "chết" theo main thread

**Đề bài:**
- Viết 1 thread chạy `while (true) { System.out.println("đang chạy nền..."); Thread.sleep(200); }`.
- Set `thread.setDaemon(true)` **trước khi** `start()`.
- Main thread: làm gì đó mất ~1 giây (vd `Thread.sleep(1000)`) rồi kết thúc (return khỏi `main`).
- Quan sát: chương trình tự dừng hẳn (kể cả thread nền), không bị chạy vô hạn — dù bạn không hề viết code nào để "tắt" thread nền đó.

**Gợi ý:** Thử bỏ `setDaemon(true)` đi rồi chạy lại, quan sát sự khác biệt (chương trình sẽ không tự kết thúc, phải tắt tay).

**Đúc kết:** Daemon thread phù hợp cho loại task nào trong thực tế? (gợi ý: log nền, healthcheck nền — việc "chết theo" ứng dụng chính là hợp lý, không cần xử lý dở dang).

---

## Thứ tự gợi ý làm trong 1 buổi (~90 phút)

1. Bài 1 (20p) → Bài 2 (15p) — nhóm Concurrency, nên làm liền nhau vì cùng 1 tư duy.
2. Bài 5 (15p) → Bài 6 (15p) — nhẹ, để "đổi vị" sau 2 bài race condition hơi nặng đầu.
3. Bài 3 (15p) → Bài 4 (15p) — nhóm Parallelism, làm sau cùng vì cần đo thời gian, nên làm khi đầu còn tỉnh để đọc số liệu cho chính xác.

**Không cần làm hết 1 buổi** — nếu hết giờ mà chưa xong, dừng đúng giờ theo quy tắc trong workbook, mai làm tiếp.
