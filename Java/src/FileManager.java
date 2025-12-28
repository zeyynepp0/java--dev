import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime; // Tarih ve Saat için
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FILE MANAGER
 * Purpose: Handles reporting to 'result.txt' with timestamps and append mode.
 *
 * @author Gemini & User
 */
public class FileManager {

    private static final String RESULT_FILE = "result.txt";
    //private static final String DB_FILE = "students_db.txt";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    // Rapor başlığı için detaylı tarih-saat formatı
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Raporu oluşturur, konsola basar ve 'result.txt' dosyasının SONUNA ekler.
     *
     * @param sessionStudents Sadece bu oturumda eklenen yeni öğrenci listesi.
     */
    public static void printAndSaveReport(List<Student> sessionStudents) {
        if (sessionStudents.isEmpty()) {
            System.out.println(">> Raporlanacak yeni veri yok.");
            return;
        }

        // 1. SIRALAMA (GPA Yüksekten Düşüğe)
        sessionStudents.sort(Comparator.comparingDouble(Student::calculateGPA).reversed());

        // 2. İSTATİSTİK HESAPLAMA
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

        // 3. RAPOR METNİNİ OLUŞTURMA
        StringBuilder sb = new StringBuilder();

        // --- YENİ: TARİH VE SAAT BAŞLIĞI ---
        sb.append("\n******************************************\n");
        sb.append("   RAPOR TARİHİ: ").append(LocalDateTime.now().format(TIMESTAMP_FMT)).append("\n");
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
        sb.append("Mid Range   : ").append(countMid).append("\n");
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
        sb.append("------------------------------------------\n"); // Raporlar arası ayırıcı çizgi

        // 4. KONSOLA YAZDIR
        System.out.println(sb.toString());

        // 5. DOSYAYA EKLE (APPEND MODE)
        // 'true' parametresi vererek dosyanın sonuna ekleme yapıyoruz (Silme yok)
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULT_FILE, true))) {
            writer.print(sb.toString());
            System.out.println(">> Rapor başarıyla '" + RESULT_FILE + "' dosyasına EKLENDİ.");
        } catch (IOException e) {
            System.err.println(">> Dosya kaydetme hatası: " + e.getMessage());
        }

        // Veritabanı güncellemesi
        //appendToDatabase(sessionStudents);
    }

    // --- DİĞER METOTLAR (Değişiklik yok) ---

//    public static List<Student> loadPreviousData() {
//        List<Student> loadedStudents = new ArrayList<>();
//       // File file = new File(DB_FILE);
//
//        if (!file.exists()) return loadedStudents;
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(",");
//                if (parts.length >= 4) {
//                    try {
//                        String name = parts[0].trim();
//                        String surname = parts[1].trim();
//                        String id = parts[2].trim();
//                        LocalDate birthDate = LocalDate.parse(parts[3].trim(), DATE_FMT);
//                        loadedStudents.add(new Student(name, surname, id, birthDate));
//                    } catch (Exception e) { }
//                }
//            }
//        } catch (IOException e) {
//            System.err.println(">> Veri yükleme hatası: " + e.getMessage());
//        }
//        return loadedStudents;
//    }

//    private static void appendToDatabase(List<Student> students) {
//        try (PrintWriter writer = new PrintWriter(new FileWriter(DB_FILE, true))) {
//            for (Student s : students) {
//                writer.println(s.getFirstName() + "," +
//                        s.getLastName() + "," +
//                        s.getStudentId() + "," +
//                        s.getFormattedBirthDate());
//            }
//        } catch (IOException e) { }
//    }
}