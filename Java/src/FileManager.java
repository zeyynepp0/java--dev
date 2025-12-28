import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DATA PERSISTENCE & DETAILED REPORTING
 * Purpose: Manages file I/O operations.
 * Updated: Saves Department info, Course list, and full Student details.
 */
public class FileManager {

    // Regex Pattern Updated for New Format:
    // "1. Name Surname - ID: 123 - Birth: 01.01.2000 - GPA: 3.50"
    private static final Pattern PATTERN = Pattern.compile(
            "\\d+\\.\\s+(.+?)\\s+-\\s+ID:\\s+(\\d+)\\s+-\\s+Birth:\\s+(.+?)\\s+-\\s+GPA:\\s+([0-9.,]+)"
    );

    /**
     * Loads student data from the previous run.
     */
    public static List<Student> loadPreviousData() {
        List<Student> list = new ArrayList<>();
        File file = new File(Constants.OUTPUT_FILE_NAME);

        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = PATTERN.matcher(line);
                if (m.find()) {
                    String fullName = m.group(1);
                    long id = Long.parseLong(m.group(2));
                    // Birth date is in the file but we don't parse it back to object for simplicity in this logic
                    // (Or you could parse it if needed, but here we just need ID and GPA to restore state)
                    double gpa = Double.parseDouble(m.group(4).replace(",", "."));

                    String[] names = fullName.split(" ", 2);
                    String first = names[0];
                    String last = (names.length > 1) ? names[1] : "";

                    // Create a placeholder student to preserve GPA
                    Student s = new Student(first, last, id, null, null);
                    s.addGrade(new Course("Historical Record", "PREV", 1), gpa);
                    list.add(s);
                }
            }
            AppLogger.log("Persistence: Loaded " + list.size() + " records from previous run.");
            System.out.println(">> SYSTEM INFO: " + list.size() + " records restored from database.");
        } catch (Exception e) {
            AppLogger.log("Error loading data: " + e.getMessage());
        }
        return list;
    }

    /**
     * Saves a FULL DETAILED report including Department, Courses, and Students.
     */
    public static void saveFullReport(Department dept, List<Course> courses, List<Student> students, String stats, String histogram) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.OUTPUT_FILE_NAME), StandardCharsets.UTF_8))) {

            // 1. DEPARTMENT INFORMATION
            writer.write("==========================================\n");
            writer.write("          DEPARTMENT INFORMATION          \n");
            writer.write("==========================================\n");
            if (dept != null) {
                writer.write("Name : " + dept.getName() + "\n");
                writer.write("Web  : " + dept // dept.toString() contains web page
                        + "\n");
            } else {
                writer.write("No department information available.\n");
            }
            writer.write("\n");

            // 2. COURSE LIST
            writer.write("==========================================\n");
            writer.write("               COURSE LIST                \n");
            writer.write("==========================================\n");
            if (courses.isEmpty()) {
                writer.write("No courses registered.\n");
            } else {
                for (Course c : courses) {
                    writer.write(String.format("- %s (%s) [%d ECTS]\n", c.getName(), c.getCode(), c.getEcts()));
                }
            }
            writer.write("\n");

            // 3. STATISTICS & HISTOGRAM
            writer.write("==========================================\n");
            writer.write("            CLASS STATISTICS              \n");
            writer.write("==========================================\n");
            writer.write(stats);
            writer.write("\n");
            writer.write(histogram);
            writer.write("\n");

            // 4. STUDENT RANKINGS (Detailed)
            writer.write("==========================================\n");
            writer.write("            STUDENT RANKINGS              \n");
            writer.write("==========================================\n");

            int rank = 1;
            for (Student s : students) {
                // Format: Rank. Name - ID - Birth: dd.mm.yyyy - GPA: 3.50
                String birthStr = (s.getAge() > 0) ? s.getAge() + " years old" : "N/A";
                // Note: Getting raw birthdate string requires a getter in Student,
                // but usually Age is sufficient. If you strictly want Date:
                // We can assume birthDate is not null for new students.

                String line = String.format(Locale.US, "%d. %s - ID: %d - Birth: %s - GPA: %.2f",
                        rank++, s.getFullName(), s.getStudentId(), birthStr, s.calculateGPA());

                writer.write(line);
                writer.newLine();
            }

            AppLogger.log("Full report saved successfully to " + Constants.OUTPUT_FILE_NAME);

        } catch (IOException e) {
            AppLogger.log("Error writing file: " + e.getMessage());
            System.err.println(">> File Write Error: " + e.getMessage());
        }
    }
}