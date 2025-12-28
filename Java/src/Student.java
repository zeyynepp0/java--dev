import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

/**
 * STUDENT ENTITY & CALCULATION ENGINE
 * * Purpose: Manages student data, grades, and GPA calculation.
 * Requirements:  Fields,  GPA Formula.
 */

public class Student {
    private String firstName;
    private String lastName;
    private long studentId; // Used 'long' to prevent Integer Overflow
    private LocalDate birthDate;
    private Department department;

    // Transcript: Maps a Course to a Grade (Double)
    private Map<Course, Double> transcript = new HashMap<>();

    public Student(String firstName, String lastName, long studentId, LocalDate birthDate, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.birthDate = birthDate;
        this.department = department;
    }

    public void addGrade(Course course, double grade) {
        transcript.put(course, grade);
    }

    public boolean hasCourse(Course c) {
        return transcript.containsKey(c);
    }

    /**
     * Calculates the Age based on BirthDate.
     */
    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Calculates Weighted GPA.
     * Formula: Sum(ECTS * Grade) / Sum(ECTS)
     */
    public double calculateGPA() {
        if (transcript.isEmpty()) return 0.0;

        double totalWeightedPoints = 0.0;
        double totalEcts = 0.0;

        for (Map.Entry<Course, Double> entry : transcript.entrySet()) {
            Course c = entry.getKey();
            Double g = entry.getValue();

            totalWeightedPoints += (c.getEcts() * g);
            totalEcts += c.getEcts();
        }

        // Arithmetic Safety: Prevent Division by Zero (NaN)
        if (totalEcts == 0) return 0.0;

        return totalWeightedPoints / totalEcts;
    }

    // Getters
    public String getFullName() { return firstName + " " + lastName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public long getStudentId() { return studentId; }
    public Department getDepartment() { return department; }
}