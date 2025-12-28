import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * MAIN APPLICATION CLASS
 * Project: Student Grading System
 * Features:
 * - Smart Duplicate Detection (Checks against ALL records)
 * - Session Reporting (Reports ONLY current session entries)
 */
public class Main {

    // Global list (Old + New) used for Validation to prevent duplicate IDs
    private static final List<Student> allStudents = new ArrayList<>();

    // Session list (Only New) used for Final Reporting
    private static final List<Student> currentSessionStudents = new ArrayList<>();

    private static final List<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        // 1. Setup Locale
        Locale.setDefault(Locale.US);

        // 2. Visual Start
        printBanner();
        AppLogger.log("System Started.");

        // 3. Persistence: Load previous records ONLY for validation
        // We add them to 'allStudents' but NOT 'currentSessionStudents'
       // List<Student> oldRecords = FileManager.loadPreviousData();
        //allStudents.addAll(oldRecords);

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

                    String name = InputHelper.getSafeText("First Name ('end' to finish):");
                    if (name.equalsIgnoreCase(Constants.CMD_END)) break;

                    String surname = InputHelper.getSafeText("Last Name:");

                    // ID Girişi (String olarak güncellendi, Student sınıfına uyumlu)
                    String id = InputHelper.getSafeText("Student ID:");

                    // --- SMART DUPLICATE CHECK (Tüm listede ara) ---
                    boolean exists = false;
                    for (Student s : allStudents) {
                        if (s.getStudentId().equals(id)) { // String karşılaştırma
                            System.out.println(">> WARNING: Student with ID " + id + " already exists!");
                            System.out.println(">> Skipping new entry.");
                            exists = true;
                            break;
                        }
                    }

                    if (exists) continue;

                    LocalDate bDate = InputHelper.getDate("Birth Date (dd.MM.yyyy):");

                    // REVIEW & CONFIRM
                    System.out.println("\n--- REVIEW STUDENT ---");
                    System.out.println("Name: " + name + " " + surname);
                    System.out.println("ID  : " + id);
                    System.out.println("Age : " + (java.time.Period.between(bDate, LocalDate.now()).getYears()));

                    if (InputHelper.getConfirmation()) {
                        // Student constructor updated to match your latest Student.java
                        // (Name, Surname, ID, BirthDate) - GPA/Dept removed from constructor
                        Student newStudent = new Student(name, surname, id, bDate);

                        // Add to global list (for validation)
                        allStudents.add(newStudent);
                        // Add to session list (for reporting)
                        currentSessionStudents.add(newStudent);

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
            // PHASE 3: COURSE ENTRY
            // =============================================================
            System.out.println("\n" + Constants.MSG_ENTER_COURSE);

            while (true) {
                try {
                    System.out.println("\n--- NEW COURSE ENTRY ---");
                    System.out.println("(Type 'cancel' at any time to reset this section)");
                    String cName = InputHelper.getSafeText("Course Name ('end' to finish):");
                    if (cName.equalsIgnoreCase(Constants.CMD_END)) break;

                    String code = InputHelper.getSafeText("Course Code:");

                    // Check duplicates
                    boolean courseExists = false;
                    for (Course c : courses) {
                        if (c.getCode().equalsIgnoreCase(code)) {
                            System.out.println(">> WARNING: Course with code '" + code + "' already exists!");
                            courseExists = true;
                            break;
                        }
                    }

                    if (courseExists) continue;

                    int ects = InputHelper.getInt("ECTS:");

                    System.out.println("\n--- REVIEW COURSE ---");
                    System.out.println("Name: " + cName + " | Code: " + code + " | ECTS: " + ects);

                    if (InputHelper.getConfirmation()) {
                        courses.add(new Course(cName, code, ects));
                        AppLogger.log("Course added: " + code);
                        System.out.println(">> Course saved.");
                    } else {
                        System.out.println(Constants.MSG_RETRY);
                    }

                } catch (InputHelper.OperationCancelledException e) {
                    System.out.println(Constants.MSG_CANCELLED);
                }
            }

            // =============================================================
            // PHASE 4: GRADE ENTRY (Only for NEW students)
            // =============================================================
            if (!currentSessionStudents.isEmpty() && !courses.isEmpty()) {
                System.out.println("\n==========================================");
                System.out.println("           GRADE ENTRY PHASE");
                System.out.println("==========================================");

                // Sadece şu an eklenen öğrencilere not giriyoruz
                for (Student s : currentSessionStudents) {
                    System.out.println("\nEntering grades for student: " + s.getFullName());

                    for (Course c : courses) {
                        try {
                            double grade = InputHelper.getGrade(c.getName());
                            s.addGrade(c, grade);
                        } catch (InputHelper.OperationCancelledException e) {
                            System.out.println(">> Grading for '" + c.getName() + "' skipped.");
                            AppLogger.log("Grade skipped: " + s.getStudentId() + " - " + c.getCode());
                        }
                    }
                }
            }

            // =============================================================
            // PHASE 5: REPORTING (Only Current Session Data)
            // =============================================================
            finalizeAndReport();

        } catch (Exception e) {
            System.err.println("\n>> CRITICAL SYSTEM ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            InputHelper.close();
            AppLogger.log("System Terminated.");
            System.out.println("\nProgram terminated successfully.");
        }
    }

    /**
     * Finalizes the process and delegates reporting to FileManager.
     * Uses 'currentSessionStudents' so only newly added data is shown.
     */
    private static void finalizeAndReport() {
        System.out.println("\n>> Generating Session Report...");

        // Sadece bu oturumda eklenenleri rapora gönderiyoruz
        FileManager.printAndSaveReport(currentSessionStudents);
    }

    private static void printBanner() {
        System.out.println("##################################################");
        System.out.println("#       STUDENT GRADING SYSTEM (SMART)           #");
        System.out.println("##################################################");
    }
}