package com.mycompany.bttlon.t5;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import javax.swing.*;

public class SearchUI extends JFrame {

    private JTextField txtFolder;
    private JTextField txtKeyword;
    private JTextArea txtLog;

    private JButton btnBrowse;
    private JButton btnSearch;

    private JFileChooser chooser;

    public SearchUI() {
        setTitle("Parallel Log Search - UI");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ====== COMPONENTS ========= //
        txtFolder = new JTextField(40);
        txtKeyword = new JTextField("login by 99", 20);

        txtLog = new JTextArea();
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtLog.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtLog);

        btnBrowse = new JButton("Browse...");
        btnSearch = new JButton("Search");

        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // ======= TOP PANEL (FOLDER + BROWSE) ======= //
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Folder: "));
        top.add(txtFolder);
        top.add(btnBrowse);

        // ======= MID PANEL (KEYWORD + SEARCH) ======= //
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mid.add(new JLabel("Keyword: "));
        mid.add(txtKeyword);
        mid.add(btnSearch);

        // PANEL WRAPPER FOR TOP + MID
        JPanel headPanel = new JPanel();
        headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.Y_AXIS));
        headPanel.add(top);
        headPanel.add(mid);

        // ======= ADD COMPONENTS TO FRAME ======= //
        add(headPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // EVENT: Browse
        btnBrowse.addActionListener(e -> {
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtFolder.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        // EVENT: Search
        btnSearch.addActionListener(e -> startSearch());
    }

    private void startSearch() {
        String folderPath = txtFolder.getText().trim();
        String keyword = txtKeyword.getText().trim();

        if (folderPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a folder!");
            return;
        }
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword!");
            return;
        }

        txtLog.setText("Starting search...\n");

        Thread t = new Thread(() -> {
            try {
                runSearch(folderPath, keyword);
            } catch (Exception ex) {
                txtLog.append("Error: " + ex.getMessage() + "\n");
            }
        });
        t.start();
    }

    private void runSearch(String folderPath, String keyword) throws IOException {
        Path dir = Paths.get(folderPath);

        List<Path> files;
        try (var stream = Files.list(dir)) {
            files = stream.filter(Files::isRegularFile).collect(Collectors.toList());
        }

        txtLog.append("Found files: " + files.size() + "\n");

        int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        txtLog.append("Searching using " + THREAD_COUNT + " threads...\n");

        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        int chunk = (files.size() + THREAD_COUNT - 1) / THREAD_COUNT;

        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            int start = i * chunk;
            int end = Math.min(start + chunk, files.size());
            int id = i;

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    searchInFile(files.get(j), keyword, results);
                }
                txtLog.append("Thread-" + id + " finished.\n");
            });
            threads[i].start();
        }

        for (Thread th : threads) {
            try {
                if (th != null) th.join();
            } catch (InterruptedException ignored) {}
        }

        // SAVE TO ketqua.txt
        Path output = Paths.get("ketqua.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            if (results.isEmpty()) {
                writer.write("No results found for keyword: " + keyword);
                txtLog.append("No results found.\n");
            } else {
                for (String r : results) {
                    writer.write(r);
                    writer.newLine();
                }
                txtLog.append("Search completed! Results saved to ketqua.txt\n");
            }
        }
    }

    private void searchInFile(Path file, String keyword, ConcurrentLinkedQueue<String> results) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.contains(keyword)) {
                    results.add("File: " + file.getFileName() + " | Line: " + lineNum + " | " + line);
                }
            }
        } catch (IOException e) {
            txtLog.append("Error reading file: " + file.toString() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchUI().setVisible(true));
    }
}
