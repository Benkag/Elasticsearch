package com.mycompany.bttlon.t5;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TimKiemLog {

    private static final String KEYWORD = "login by 99";
    private static final Path LOG_DIR = Paths.get("logs");
    private static final Path OUTPUT_FILE = Paths.get("ketqua.txt");

    private static class SearchResult {
        final String fileName;
        final int lineNumber;
        final String content;

        SearchResult(String fileName, int lineNumber, String content) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.content = content;
        }
    }

    //================= THREAD WORKER =================//
    private static class SearchThread extends Thread {
        private final List<Path> assignedFiles;
        private final ConcurrentLinkedQueue<SearchResult> results;

        SearchThread(List<Path> assignedFiles,
                     ConcurrentLinkedQueue<SearchResult> results) {
            this.assignedFiles = assignedFiles;
            this.results = results;
        }

        @Override
        public void run() {
            for (Path file : assignedFiles) {
                searchInFile(file);
            }
        }

        private void searchInFile(Path file) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                int lineNum = 0;

                while ((line = reader.readLine()) != null) {
                    lineNum++;

                    if (line.contains(KEYWORD)) {
                        results.add(new SearchResult(
                                file.getFileName().toString(),
                                lineNum,
                                line
                        ));
                    }
                }

            } catch (IOException e) {
                System.err.println("Lỗi khi đọc file: " + file);
            }
        }
    }
    //================= END THREAD =================//

    public static void main(String[] args) throws Exception {

        if (!Files.exists(LOG_DIR)) {
            System.err.println("Không tìm thấy thư mục logs!");
            return;
        }

        // Lấy toàn bộ file log
        List<Path> allFiles;
        try (var stream = Files.list(LOG_DIR)) {
            allFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().startsWith("log_"))
                    .toList();
        }

        System.out.println("Số file cần quét: " + allFiles.size());

        // Chọn số thread = số lõi CPU
        int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        System.out.println("Sử dụng số Thread: " + THREAD_COUNT);

        ConcurrentLinkedQueue<SearchResult> results = new ConcurrentLinkedQueue<>();

        // Chia file cho các thread
        List<SearchThread> threads = new ArrayList<>();
        int chunkSize = (allFiles.size() + THREAD_COUNT - 1) / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, allFiles.size());

            if (start >= end)
                break;

            List<Path> slice = allFiles.subList(start, end);

            SearchThread t = new SearchThread(slice, results);
            threads.add(t);
            t.start();
        }

        // Chờ tất cả thread chạy xong
        for (Thread t : threads) {
            t.join();
        }

        // Ghi kết quả ra file ketqua.txt
        try (BufferedWriter writer = Files.newBufferedWriter(OUTPUT_FILE)) {
            if (results.isEmpty()) {
                writer.write("Không tìm thấy log chứa: " + KEYWORD);
                writer.newLine();
            } else {
                for (SearchResult r : results) {
                    writer.write("File: " + r.fileName + " | Dòng: " + r.lineNumber);
                    writer.newLine();
                    writer.write("Nội dung: " + r.content);
                    writer.newLine();
                    writer.newLine();
                }
            }
        }

        System.out.println("Đã ghi kết quả vào file: " + OUTPUT_FILE.toAbsolutePath());
    }
}
