import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static javax.swing.SwingUtilities.invokeLater;

public class WebFrame extends JFrame {
    private DefaultTableModel model;
    private JButton singleFetchButton;
    private JButton concurrentFetchButton;
    private JButton stopButton;
    private JLabel completedLabel;
    private JLabel runningLabel;
    private JLabel elapsedLabel;
    private JTextField limitField;
    private JProgressBar progressBar;

    private Thread launcherThread;
    private final Set<WebWorker> workers;
    private Semaphore semaphore;
    private volatile int completedCount;
    private volatile int runningCount;
    private volatile boolean stopped;
    private final Object lock = new Object();
    private long startTime;

    public WebFrame() {
        super("WebLoader");
        workers = Collections.synchronizedSet(new HashSet<>());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        loadUrls();

        pack();
        setVisible(true);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[] {"url", "status"}, 0);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        singleFetchButton = new JButton("Single Thread Fetch");
        concurrentFetchButton = new JButton("Concurrent Fetch");
        stopButton = new JButton("Stop");
        limitField = new JTextField("4", 3);
        limitField.setMaximumSize(new Dimension(50, 25));

        buttonPanel.add(singleFetchButton);
        buttonPanel.add(concurrentFetchButton);
        buttonPanel.add(new JLabel("Threads:"));
        buttonPanel.add(limitField);
        buttonPanel.add(stopButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        completedLabel = new JLabel("Completed: 0");
        runningLabel = new JLabel("Running: 0");
        elapsedLabel = new JLabel("Elapsed: 0.0s");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        statusPanel.add(completedLabel);
        statusPanel.add(runningLabel);
        statusPanel.add(elapsedLabel);
        statusPanel.add(progressBar);

        controlPanel.add(buttonPanel);
        controlPanel.add(statusPanel);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        singleFetchButton.addActionListener(e -> startFetch(1));
        concurrentFetchButton.addActionListener(e -> {
            int limit;
            try {
                limit = Integer.parseInt(limitField.getText().trim());
                if (limit < 1) limit = 1;
            } catch (NumberFormatException ex) {
                limit = 4;
                limitField.setText("4");
            }
            startFetch(limit);
        });

        stopButton.addActionListener(e -> stopFetch());

        stopButton.setEnabled(false);

        add(mainPanel);
    }

    private void loadUrls() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("links.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    model.addRow(new Object[] {line, ""});
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading URLs: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startFetch(int limit) {
        synchronized (lock) {
            singleFetchButton.setEnabled(false);
            concurrentFetchButton.setEnabled(false);
            stopButton.setEnabled(true);

            completedCount = 0;
            runningCount = 0;
            stopped = false;

            updateLabels();

            int rowCount = model.getRowCount();
            progressBar.setMaximum(rowCount);
            progressBar.setValue(0);

            for (int i = 0; i < rowCount; i++) {
                model.setValueAt("", i, 1);
            }

            semaphore = new Semaphore(limit);
            startTime = System.currentTimeMillis();

            launcherThread = new Thread(this::launchWorkers);
            launcherThread.start();

            incrementRunningCount();
        }
    }

    private void launchWorkers() {
        try {
            int rowCount = model.getRowCount();

            for (int i = 0; i < rowCount && !Thread.currentThread().isInterrupted() && !stopped; i++) {
                semaphore.acquire();

                if (Thread.currentThread().isInterrupted() || stopped) {
                    semaphore.release();
                    break;
                }

                String url = (String) model.getValueAt(i, 0);
                WebWorker worker = new WebWorker(url, i, this);
                workers.add(worker);
                worker.start();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            decrementRunningCount();
        }
    }

    private void stopFetch() {
        synchronized (lock) {
            stopped = true;

            if (launcherThread != null) {
                launcherThread.interrupt();
            }

            for (WebWorker worker : new HashSet<>(workers)) {
                worker.interrupt();
            }

            workers.clear();
        }
    }

    public void workerFinished(WebWorker worker, int row, String status) {
        invokeLater(() -> {
            model.setValueAt(status, row, 1);
            workers.remove(worker);
            semaphore.release();
            incrementCompletedCount();
            decrementRunningCount();

            if (runningCount == 0) {
                fetchCompleted();
            }
        });
    }

    private void fetchCompleted() {
        long elapsed = System.currentTimeMillis() - startTime;
        double seconds = elapsed / 1000.0;

        elapsedLabel.setText(String.format("Elapsed: %.1fs", seconds));

        singleFetchButton.setEnabled(true);
        concurrentFetchButton.setEnabled(true);
        stopButton.setEnabled(false);
        progressBar.setValue(0);
    }

    public void incrementCompletedCount() {
        completedCount++;
        completedLabel.setText("Completed: " + completedCount);
        progressBar.setValue(completedCount);
    }

    public void incrementRunningCount() {
        runningCount++;
        updateLabels();
    }

    public void decrementRunningCount() {
        runningCount--;
        updateLabels();
    }

    private void updateLabels() {
        runningLabel.setText("Running: " + runningCount);
    }

    public static void main(String[] args) {
        invokeLater(WebFrame::new);
    }
}