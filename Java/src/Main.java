import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * MAIN APPLICATION CLASS
 * * Project: Student Grading System v5.0 (Smart & Ultimate Edition)
 * Features:
 * - Smart Duplicate Detection (Student ID, Course Code)
 * - Smart Grading (Only asks for missing grades)
 * - Persistence, Audit Logging, Robust Validation, Cancel/Retry
 * * Compliance: Fully meets PDF requirements.
 */
public class Main {

    // Main data repositories
    private static final List<Student> students = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        // 1. Setup Locale
        Locale.setDefault(Locale.US);

        // 2. Visual Start
        printBanner();
        AppLogger.log("System Started.");

        // 3. Persistence: Load previous records
        List<Student> oldRecords = FileManager.loadPreviousData();
        students.addAll(oldRecords);

        try {
            // =============================================================
            // PHASE 1: DEPARTMENT ENTRY
            // =============================================================
            Department dept = null;
            boolean deptConfirmed = false;

            while (!deptConfirmed) {
                try {
                    System.out.println("\n" + Constants.MSG_ENTER_DEPT);
                    System.out.println("(Type 'cancel' at any time to reset this section)");

                    String dName = InputHelper.getSafeText("Department Name:");
                    String dWeb = InputHelper.getValidWebPage("Web Page:");
                    LocalDate dDate = InputHelper.getDate("Est. Date (dd.MM.yyyy):");

                    System.out.println("\n--- REVIEW DEPARTMENT ---");
                    System.out.println("Name: " + dName);
                    System.out.println("Web : " + dWeb);
                    System.out.println("Date: " + dDate);

                    if (InputHelper.getConfirmation()) {
                        dept = new Department(dName, dWeb, dDate);
                        deptConfirmed = true;
                        AppLogger.log("Department set: " + dName);
                    } else {
                        System.out.println(Constants.MSG_RETRY);
                    }

                } catch (InputHelper.OperationCancelledException e) {
                    System.out.println(">> Department entry is mandatory. Resetting form...");
                }
            }

            // =============================================================
            // PHASE 2: STUDENT ENTRY (Smart Duplicate Check)
            // =============================================================
            System.out.println("\n" + Constants.MSG_ENTER_STUDENT);

            while (true) {
                try {
                    System.out.println("\n--- NEW STUDENT ENTRY ---");
                    System.out.println("(Type 'cancel' at any time to reset this section)");
                    // Name Input
                    String name = InputHelper.getSafeText("First Name ('end' to finish):");
                    if (name.equalsIgnoreCase(Constants.CMD_END)) break;

                    String surname = InputHelper.getSafeText("Last Name:");

                    // --- SMART DUPLICATE CHECK ---
                    long id = InputHelper.getLong("Student ID:");

                    boolean exists = false;
                    for (Student s : students) {
                        if (s.getStudentId() == id) {
                            System.out.println(">> WARNING: Student with ID " + id + " already exists!");
                            System.out.println(">> Skipping new entry. Existing student will be used.");
                            exists = true;
                            break;
                        }
                    }

                    // Eğer öğrenci varsa, yeni kayıt oluşturma adımını atla ve döngünün başına dön
                    if (exists) {
                        continue;
                    }

                    LocalDate bDate = InputHelper.getDate("Birth Date (dd.MM.yyyy):");

                    // REVIEW & CONFIRM
                    System.out.println("\n--- REVIEW STUDENT ---");
                    System.out.println("Name: " + name + " " + surname);
                    System.out.println("ID  : " + id);
                    System.out.println("Age : " + (java.time.Period.between(bDate, LocalDate.now()).getYears()));

                    if (InputHelper.getConfirmation()) {
                        Student newStudent = new Student(name, surname, id, bDate, dept);
                        students.add(newStudent);
                        AppLogger.log("Student added: ID " + id);
                        System.out.println(">> Student saved successfully.");
                    } else {
                        System.out.println(Constants.MSG_RETRY);
                    }

                } catch (InputHelper.OperationCancelledException e) {
                    System.out.println(Constants.MSG_CANCELLED);
                }
            }

            // =============================================================
            // PHASE 3: COURSE ENTRY (Smart Duplicate Check)
            // =============================================================
            System.out.println("\n" + Constants.MSG_ENTER_COURSE);

            while (true) {
                try {
                    System.out.println("\n--- NEW COURSE ENTRY ---");
                    System.out.println("(Type 'cancel' at any time to reset this section)");
                    String cName = InputHelper.getSafeText("Course Name ('end' to finish):");
                    if (cName.equalsIgnoreCase(Constants.CMD_END)) break;

                    String code = InputHelper.getSafeText("Course Code:");

                    // --- SMART DUPLICATE CHECK ---
                    boolean courseExists = false;
                    for (Course c : courses) {
                        if (c.getCode().equalsIgnoreCase(code)) {
                            System.out.println(">> WARNING: Course with code '" + code + "' already exists!");
                            System.out.println(">> Skipping new entry.");
                            courseExists = true;
                            break;
                        }
                    }

                    if (courseExists) {
                        continue;
                    }

                    int ects = InputHelper.getInt("ECTS:");

                    // REVIEW & CONFIRM
                    System.out.println("\n--- REVIEW COURSE ---");
                    System.out.println("Name: " + cName);
                    System.out.println("Code: " + code);
                    System.out.println("ECTS: " + ects);

                    if (InputHelper.getConfirmation()) {
                        courses.add(new Course(cName, code, ects));
                        AppLogger.log("Course added: " + code);
                        System.out.println(">> Course saved successfully.");
                    } else {
                        System.out.println(Constants.MSG_RETRY);
                    }

                } catch (InputHelper.OperationCancelledException e) {
                    System.out.println(Constants.MSG_CANCELLED);
                }
            }

            // =============================================================
            // PHASE 4: GRADE ENTRY (Smart Filling)
            // =============================================================
            if (!students.isEmpty() && !courses.isEmpty()) {
                System.out.println("\n==========================================");
                System.out.println("           GRADE ENTRY PHASE");
                System.out.println("==========================================");
                System.out.println("(Checking for missing grades...)");

                for (Student s : students) {
                    // Artık eski kayıtları tamamen atlamıyoruz.
                    // Her ders için kontrol yapıyoruz: Öğrencinin o dersi var mı?

                    boolean headerPrinted = false;

                    for (Course c : courses) {
                        // --- SMART CHECK: Öğrenci bu dersi zaten almış mı? ---
                        // (Not: Student sınıfına 'hasCourse' metodunu eklediğini varsayıyoruz)
                        if (s.hasCourse(c)) {
                            continue; // Notu var, tekrar sorma.
                        }

                        if (!headerPrinted) {
                            System.out.println("\nEntering grades for student: " + s.getFullName());
                            headerPrinted = true;
                        }

                        try {
                            double grade = InputHelper.getGrade(c.getName());
                            s.addGrade(c, grade);
                        } catch (InputHelper.OperationCancelledException e) {
                            System.out.println(">> Grading for '" + c.getName() + "' skipped by user.");
                            AppLogger.log("Grade skipped for student " + s.getStudentId() + ", course " + c.getCode());
                        }
                    }
                }
            }

            // =============================================================
            // PHASE 5: REPORTING & FINALIZATION
            // =============================================================
            finalizeAndReport(dept);

        } catch (Exception e) {
            System.err.println("\n>> CRITICAL SYSTEM ERROR: " + e.getMessage());
            AppLogger.log("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            InputHelper.close();
            AppLogger.log("System Terminated.");
            System.out.println("\nProgram terminated successfully (Exit Code 0).");
        }
    }

    private static void finalizeAndReport(Department dept) {
        if (students.isEmpty()) {
            System.out.println(">> No data available to report.");
            return;
        }

        students.sort(Comparator.comparingDouble(Student::calculateGPA).reversed());

        String stats = generateStats();
        String histogram = generateHistogram();

        System.out.println("\n" + stats);
        System.out.println(histogram);
        System.out.println("--------------------------------------------------");

        int rank = 1;
        for (Student s : students) {
            System.out.printf(Locale.US, "%d. %s - ID: %d - GPA: %.2f (Age: %d)%n",
                    rank++, s.getFullName(), s.getStudentId(), s.calculateGPA(), s.getAge());
        }

        FileManager.saveFullReport(dept, courses, students, stats, histogram);
        System.out.println("\n>> Final results saved to '" + Constants.OUTPUT_FILE_NAME + "'.");
    }

    private static String generateStats() {
        double totalGpa = 0;
        double max = -1;
        double min = 5;

        for (Student s : students) {
            double gpa = s.calculateGPA();
            totalGpa += gpa;
            if (gpa > max) max = gpa;
            if (gpa < min) min = gpa;
        }
        double avg = totalGpa / students.size();

        return String.format(Locale.US,
                "CLASS ANALYTICS REPORT:\n- Total Students: %d\n- Class Average : %.2f\n- Highest GPA   : %.2f\n- Lowest GPA    : %.2f",
                students.size(), avg, max, min);
    }

    private static String generateHistogram() {
        int countAA = 0, countBB = 0, countFail = 0, countOthers = 0;

        for (Student s : students) {
            double gpa = s.calculateGPA();
            if (gpa >= 4.0) countAA++;
            else if (gpa >= 3.0) countBB++;
            else if (gpa == 0.0) countFail++;
            else countOthers++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== GPA DISTRIBUTION (HISTOGRAM) ===\n");
        sb.append("4.00 [High] : ").append("*".repeat(countAA)).append("\n");
        sb.append("3.xx [Good] : ").append("*".repeat(countBB)).append("\n");
        sb.append("Mid Range   : ").append("*".repeat(countOthers)).append("\n");
        sb.append("0.00 [Fail] : ").append("*".repeat(countFail)).append("\n");
        return sb.toString();
    }

    private static void printBanner() {
        System.out.println("##################################################");
        System.out.println("#                                                #");
        System.out.println("#       STUDENT GRADING SYSTEM (SMART)           #");
        System.out.println("#                                                #");
        System.out.println("##################################################");
    }
}