/**
 * COURSE ENTITY
 * * Purpose: Stores course details and ensures ECTS validity.
 * Requirements:  Name, Code, ECTS.
 */
import java.util.Objects;

public class Course {
    private String name;
    private String code;
    private int ects;

    public Course(String name, String code, int ects) {
        // Logic Check: Defensive programming against negative credits
        if (ects < 0) {
            throw new IllegalArgumentException("ECTS cannot be negative.");
        }
        this.name = name;
        this.code = code;
        this.ects = ects;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public int getEcts() { return ects; }

    @Override
    public String toString() { return name + " (" + code + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}