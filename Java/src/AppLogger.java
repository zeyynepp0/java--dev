import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AUDIT LOGGER
 * * Purpose: Records all system activities and errors to a log file ('app.log')
 * with precise timestamps for tracking and debugging.
 */
public class AppLogger {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String action) {
        // 'true' enables append mode so we don't overwrite previous logs
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.LOG_FILE_NAME, true))) {
            String logEntry = String.format("[%s] %s", dtf.format(LocalDateTime.now()), action);
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println(">> Logger Error: " + e.getMessage());
        }
    }
}