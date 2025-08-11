import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebWorker extends Thread {
    private final String urlString;
    private final int row;
    private final WebFrame frame;

    public WebWorker(String urlString, int row, WebFrame frame) {
        this.urlString = urlString;
        this.row = row;
        this.frame = frame;
    }

    @Override
    public void run() {
        frame.incrementRunningCount();

        try {
            String status = download();
            frame.workerFinished(this, row, status);
        } catch (Exception e) {
            frame.workerFinished(this, row, "error: " + e.getMessage());
        }
    }

    private String download() {
        InputStream input = null;
        StringBuilder contents;
        long startTime = System.currentTimeMillis();

        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            connection.setConnectTimeout(5000);
            connection.connect();

            input = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);

            while ((len = reader.read(array, 0, array.length)) > 0) {
                contents.append(array, 0, len);
                Thread.sleep(100);

                if (Thread.currentThread().isInterrupted()) {
                    return "interrupted";
                }
            }

            long endTime = System.currentTimeMillis();
            long elapsed = endTime - startTime;

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(new Date()) + " " + elapsed + "ms " + contents.length() + " bytes";

        } catch (IOException ignored) {
            return "err";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "interrupted";
        } finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) { }
        }
    }
}