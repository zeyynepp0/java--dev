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

    public Department(String name, String webPage, LocalDate establishmentDate) {
        this.name = name;
        this.webPage = webPage;
        this.establishmentDate = establishmentDate;
    }

    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + webPage + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(webPage, that.webPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webPage);
    }
}