import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * STUDENT ENTITY & CALCULATION ENGINE
 * Purpose: Manages student data and calculates GPA dynamically based on courses.
 *
 * @author Zeynep Can
 */
public class Student {

    // --- Fields ---
    private String firstName;
    private String lastName;
    private String studentId; // String is better for IDs (preserves leading zeros)
    private LocalDate birthDate;

    // Transcript: Maps a Course to a Grade (Double)
    // Map<Ders, Not> şeklinde tutuyoruz.
    private Map<Course, Double> transcript = new HashMap<>();

    // --- Constructor ---

    /**
     * Constructs a new Student.
     * Note: GPA is NOT passed here anymore, it is calculated later.
     *
     * @param firstName  First Name
     * @param lastName   Last Name
     * @param studentId  Student ID
     * @param birthDate  Date of Birth
     */
    public Student(String firstName, String lastName, String studentId, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.birthDate = birthDate;
    }

    // --- Core Logic: Grade Management ---

    /**
     * Adds a course and its grade to the student's transcript.
     * @param course The course object (must have ECTS).
     * @param grade  The grade received (0.00 - 4.00).
     */
    public void addGrade(Course course, double grade) {
        if (course != null) {
            transcript.put(course, grade);
        }
    }

    /**
     * Checks if the student has taken a specific course.
     */
    public boolean hasCourse(Course c) {
        return transcript.containsKey(c);
    }

    /**
     * Calculates Weighted GPA dynamically.
     * Formula: Sum(ECTS * Grade) / Sum(ECTS)
     * * @return Calculated GPA (0.00 - 4.00)
     */
    public double calculateGPA() {
        if (transcript.isEmpty()) return 0.0;

        double totalWeightedPoints = 0.0;
        double totalEcts = 0.0;

        for (Map.Entry<Course, Double> entry : transcript.entrySet()) {
            Course c = entry.getKey();
            Double grade = entry.getValue();

            // Güvenlik kontrolü: Dersin ECTS'i var mı?
            double ects = c.getEcts();

            totalWeightedPoints += (ects * grade);
            totalEcts += ects;
        }

        // Division by Zero check
        if (totalEcts == 0) return 0.0;

        return totalWeightedPoints / totalEcts;
    }

    // --- Helper Methods ---

    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getFormattedBirthDate() {
        if (birthDate == null) return "N/A";
        return birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // --- Getters & Setters ---

    // FileManager veya Main sınıfı "getGpa()" çağırdığında hesaplama çalışsın:
    public double getGpa() {
        return calculateGPA();
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    @Override
    public String toString() {
        return "Student{" +
                "Name='" + getFullName() + '\'' +
                ", ID='" + studentId + '\'' +
                ", GPA=" + String.format("%.2f", calculateGPA()) +
                '}';
    }
}