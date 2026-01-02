import java.time.LocalDate;
import java.util.Objects;
/**
 * DEPARTMENT ENTITY
 * * Purpose: Stores department details.
 * Requirements:  Name, Web Page, Establishment Date.
 */
public class Department {
    private String name;
    private String webPage;
    private LocalDate establishmentDate;

    /**
     * Creates a new Department instance.
     *
     * @param name the name of the department
     * @param webPage the official web page of the department
     * @param establishmentDate the date the department was established
     */

    public Department(String name, String webPage, LocalDate establishmentDate) {
        this.name = name;
        this.webPage = webPage;
        this.establishmentDate = establishmentDate;
    }

    /**
     * @return the name of the department
     */
    public String getName() { return name; }

    /**
     * Returns a readable string representation of the department.
     *
     * @return formatted department name and web page
     */
    @Override
    public String toString() {
        return name + " (" + webPage + ")";
    }


    /**
     * Compares departments based on their web page.
     *
     * @param o the object to compare
     * @return true if both departments have the same web page
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(webPage, that.webPage);
    }

    /**
     * Generates hash code based on department web page.
     *
     * @return hash code of the department
     */
    @Override
    public int hashCode() {
        return Objects.hash(webPage);
    }
}