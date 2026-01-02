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
    /**
     * Creates a new Course instance.
     *
     * @param name the name of the course
     * @param code the unique course code
     * @param ects the ECTS credit value (must be non-negative)
     * @throws IllegalArgumentException if ects is negative
     */
    public Course(String name, String code, int ects) {
        // Logic Check: Defensive programming against negative credits
        if (ects < 0) {
            throw new IllegalArgumentException("ECTS cannot be negative.");
        }
        this.name = name;
        this.code = code;
        this.ects = ects;
    }

    /**
     * @return the course name
     */

    public String getName() { return name; }

    /**
     * @return the course code
     */
    public String getCode() { return code; }

    /**
     * @return the ECTS credit value
     */
    public int getEcts() { return ects; }

    /**
     * Returns a readable string representation of the course.
     *
     * @return formatted course name and code
     */
    @Override
    public String toString() { return name + " (" + code + ")"; }


    /**
     * Compares courses based on their course code.
     *
     * @param o the object to compare
     * @return true if both courses have the same code
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }

    /**
     * Generates hash code based on course code.
     *
     * @return hash code of the course
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}