import java.io.*;
import java.time.LocalDateTime; // For Date and Time
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FILE MANAGER
 * Purpose: Handles reporting to 'result.txt' with timestamps and append mode.
 *
 * @author Zeynep Can
 */
public class FileManager {

    private static final String RESULT_FILE = "result.txt";


    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Generates the report, prints it to the console, and appends it to the END of the 'result.txt' file. *
     * @param sessionStudents Only the list of new students added in this session.
     */
    public static void printAndSaveReport(List<Student> sessionStudents) {
        if (sessionStudents.isEmpty()) {
            System.out.println(">> There is no new data to report.");
            return;
        }

        // 1. RANKING (GPA High to Low)
        sessionStudents.sort(Comparator.comparingDouble(Student::calculateGPA).reversed());

        // 2. STATISTICAL CALCULATION
        double totalGpa = 0;
        double maxGpa = Double.MIN_VALUE;
        double minGpa = Double.MAX_VALUE;
        int countHigh = 0;
        int countGood = 0;
        int countMid = 0;
        int countFail = 0;

        for (Student s : sessionStudents) {
            double gpa = s.calculateGPA();
            totalGpa += gpa;
            if (gpa > maxGpa) maxGpa = gpa;
            if (gpa < minGpa) minGpa = gpa;

            if (gpa >= 3.50) countHigh++;
            else if (gpa >= 3.00) countGood++;
            else if (gpa >= 2.00) countMid++;
            else countFail++;
        }
        double avg = totalGpa / sessionStudents.size();

        // 3. Creating the report text
        StringBuilder sb = new StringBuilder();

        // ---  DATE AND TIME HEADING ---
        sb.append("\n******************************************\n");
        sb.append("   REPORT DATE: ").append(LocalDateTime.now().format(TIMESTAMP_FMT)).append("\n");
        sb.append("******************************************\n");

        sb.append("==========================================\n");
        sb.append("            CLASS STATISTICS              \n");
        sb.append("==========================================\n");
        sb.append("CLASS ANALYTICS REPORT:\n");
        sb.append("- Total Students: ").append(sessionStudents.size()).append("\n");
        sb.append(String.format(Locale.US, "- Class Average : %.2f%n", avg));
        sb.append(String.format(Locale.US, "- Highest GPA   : %.2f%n", maxGpa));
        sb.append(String.format(Locale.US, "- Lowest GPA    : %.2f%n", minGpa));

        sb.append("\n=== GPA DISTRIBUTION (HISTOGRAM) ===\n");
        sb.append("4.00 [High] : ").append(countHigh).append("\n");
        sb.append("3.xx [Good] : ").append(countGood).append("\n");
        sb.append("Mid-Range   : ").append(countMid).append("\n");
        sb.append("0.00 [Fail] : ").append(countFail).append("\n");

        sb.append("\n==========================================\n");
        sb.append("            STUDENT RANKINGS              \n");
        sb.append("==========================================\n");

        int rank = 1;
        for (Student s : sessionStudents) {
            sb.append(String.format(Locale.US, "%d. %s - ID: %s - Birth: %s - GPA: %.2f%n",
                    rank++,
                    s.getFullName(),
                    s.getStudentId(),
                    s.getFormattedBirthDate(),
                    s.calculateGPA()));
        }
        sb.append("==========================================\n");
        sb.append("------------------------------------------\n");

        // 4. PRINT TO CONSOLE
        System.out.println(sb.toString());

        // 5. APPEND MODE
        // We are appending to the end of the file by providing the 'true' parameter (No deletion)
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULT_FILE, true))) {
            writer.print(sb.toString());
            System.out.println(">> The report has been successfully ADDED to the '" + RESULT_FILE + "' file.");
        } catch (IOException e) {
            System.err.println(">>File saving error: " + e.getMessage());
        }


    }


}