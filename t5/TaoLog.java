package com.mycompany.bttlon.t5;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaoLog {

    private static final int NUM_FILES = 3000;          // số file log
    private static final int LINES_PER_FILE = 20000;    // số dòng mỗi file
    private static final Path LOG_DIR = Paths.get("logs");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd_MM_yy");

    //================= THREAD TẠO FILE =================//
    private static class WriterThread extends Thread {
        private final int startIndex;   // file bắt đầu (theo chỉ số)
        private final int endIndex;     // file kết thúc (exclusive)
        private final LocalDate startDate;

        WriterThread(int startIndex, int endIndex, LocalDate startDate, String name) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.startDate = startDate;
            this.setName(name);
        }

        @Override
        public void run() {
            // Mỗi thread dùng 1 Random riêng (tránh tranh chấp)
            Random random = new Random(System.nanoTime() ^ getId());

            for (int i = startIndex; i < endIndex; i++) {
                LocalDate date = startDate.plusDays(i);
                String fileName = "log_" + date.format(FMT) + ".txt";
                Path file = LOG_DIR.resolve(fileName);

                try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                    for (int line = 1; line <= LINES_PER_FILE; line++) {
                        String logLine = generateLogLine(date, line, random);
                        writer.write(logLine);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.err.println("[" + getName() + "] Lỗi khi ghi file: " + file + " - " + e.getMessage());
                }
            }
        }
    }
    //================= HẾT THREAD =================//

    // Hàm tạo nội dung từng dòng log
    private static String generateLogLine(LocalDate date, int lineNum, Random random) {
        int x = random.nextInt(1000); // 0..999

        // Xác suất rất nhỏ để có "login by 99"
        if (x == 0) {
            return String.format("[%s] INFO  user=99 action=login by 99 from=127.0.0.1 line=%d",
                    date, lineNum);
        } else {
            return String.format("[%s] DEBUG some other event occurred at line=%d value=%d",
                    date, lineNum, x);
        }
    }

    public static void main(String[] args) throws Exception {
        // Tạo thư mục logs (an toàn nếu tồn tại sẵn)
        Files.createDirectories(LOG_DIR);

        // Ngày bắt đầu để sinh tên file log_dd_MM_yy.txt
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        // Số thread lấy theo số lõi CPU
        int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        System.out.println("Sử dụng " + THREAD_COUNT + " thread để tạo " + NUM_FILES + " file log.");

        List<WriterThread> threads = new ArrayList<>();

        // Chia đều 3000 file cho các thread
        int chunkSize = (NUM_FILES + THREAD_COUNT - 1) / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, NUM_FILES);

            if (start >= end) {
                break;
            }

            WriterThread t = new WriterThread(start, end, startDate, "Writer-" + i);
            threads.add(t);
            t.start();
        }

        // Chờ tất cả thread hoàn thành
        for (WriterThread t : threads) {
            t.join();
        }

        System.out.println("Đã tạo xong " + NUM_FILES + " file log trong thư mục: "
                + LOG_DIR.toAbsolutePath());
    }
}
