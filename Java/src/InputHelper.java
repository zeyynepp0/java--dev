import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * INPUT MANAGER & VALIDATOR
 * * Purpose: Handles all user input safely using try-catch blocks.
 * Ensures the program never crashes due to invalid input.
 * Requirement: "Unexpected inputs must be taken into account".
 */
public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    // 1-We will throw this error when the user abandons the project.
    public static class OperationCancelledException extends RuntimeException {}

    // --- 2- COMMAND CONTROL ---
    private static void checkCommand(String input) {
        if (input.equalsIgnoreCase(Constants.CMD_CANCEL)) {
            throw new OperationCancelledException();
        }
    }
    // --- 3. GETTER FOR SCANNER (Fixes your error) ---
    // Allows Main.java to access scanner for raw input checks (e.g. checking for 'end')
    public static Scanner getScanner() {
        return scanner;
    }
    public static boolean getConfirmation() {
        while (true) {
            System.out.print(Constants.MSG_CONFIRM);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("evet") || input.equals("e")) {
                return true;
            }
            if (input.equals("n") || input.equals("no") || input.equals("hayır") || input.equals("h")) {
                return false;
            }
        }
    }

    /**
     * Validates that the name contains only letters (Regex).
     */
    public static String getValidName(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            checkCommand(input);
            // Regex: Letters, spaces, dots, hyphens allowed. No numbers.
            if (input.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ ]+")) {
                return input;
            }
            System.out.println(Constants.ERR_INVALID_NAME);
        }
    }


    public static int getInt(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            checkCommand(input);

            try {
                int val = Integer.parseInt(input);
                if (val <= 0) { System.out.println(Constants.ERR_NEGATIVE); continue; }
                return val;
            } catch (NumberFormatException e) {
                System.out.println(Constants.ERR_INVALID_NUMBER);
            }
        }
    }


    public static LocalDate getDate(String prompt) {
        // Çoklu format desteği için dizimiz (İstersen sadece DATE_FMT de kullanabilirsin)
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        };

        while (true) {
            System.out.print(prompt + " ");


            String input = scanner.nextLine().trim();

            checkCommand(input);

            if (input.isEmpty()) {
                System.out.println(Constants.ERR_EMPTY);
                continue;
            }

            try {
                LocalDate date = null;


                for (DateTimeFormatter fmt : formatters) {
                    try {

                        date = LocalDate.parse(input, fmt);
                        break;
                    } catch (DateTimeParseException ignored) {

                    }
                }


                if (date == null) {
                    throw new DateTimeParseException("Invalid format", input, 0);
                }


                if (date.isAfter(LocalDate.now())) {
                    System.out.println(Constants.ERR_FUTURE_DATE);
                    continue;
                }

                return date;

            } catch (DateTimeParseException e) {
                // 4. Senin istediğin hata mesajı burada basılıyor
                System.out.println(Constants.ERR_DATE_FMT);
            }
        }
    }

    public static String getSafeText(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            checkCommand(input);

            if (input.isEmpty()) {
                System.out.println(Constants.ERR_EMPTY);
                continue;
            }


            if (input.matches("[a-zA-Z0-9ğüşıöçĞÜŞİÖÇ ]+")) {
                return input;
            }

            System.out.println(">> ERROR: Invalid character! (You cannot use +, -, *, ? etc.).");
            System.out.println(">> Please use only letters, numbers, and spaces.");
        }
    }
    /**
     * Web Address Format Check
     * Requires the user to enter a valid URL format. * Example: www.site.com, site.edu.tr
     */
    public static String getValidWebPage(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();
            checkCommand(input);

            if (input.isEmpty()) {
                System.out.println(Constants.ERR_EMPTY);
                continue;
            }

            // Regex Explanation:
            // ^(https?://)? -> Optionally starts with http:// or https://
            // (www\.)? -> Optionally starts with www.
            // [\w-]+ -> Domain name containing letters, numbers, or hyphens (-)
            // \.[a-z]{2,} -> Dot and at least 2-letter extension (.com, .edu, etc.)
            // (\.[a-z]{2,})? -> Optional second extension (.tr, etc.)
            // Example valid entries: duzce.edu.tr, www.google.com, http://site.net
            String urlRegex = "^(https?://)?(www\\.)?[\\w-]+\\.[a-z]{2,}(\\.[a-z]{2,})?$";

            if (input.matches(urlRegex)) {
                return input;
            }

            System.out.println(">> ERROR: Invalid web address format!");
            System.out.println(">> Please enter the address in the format 'www.duzce.edu.tr' or 'site.com'.");
        }
    }
    /**
     * Converts Letter Grade to Numerical Value
     */
    public static double getGrade(String courseName) {
        while (true) {
            System.out.println("Enter the course grade for " + courseName + " ('cancel' to skip):");
            System.out.print(">> Grade (AA, BA...): ");
            String input = scanner.nextLine().trim(); // toUpperCase'i sonra yapıyoruz
            checkCommand(input);
            switch (input.toUpperCase()) {
                case "AA": return 4.00;
                case "BA": return 3.50;
                case "BB": return 3.25;
                case "CB": return 3.00;
                case "CC": return 2.50;
                case "DC": return 2.25;
                case "DD": return 2.00;
                case "FD": return 1.50;
                case "FF": return 0.00;
                default:
                    System.out.println(">> ERROR: Invalid grade code. Please use the table (AA-FF).");
            }
        }
    }

    public static void close() { scanner.close(); }
}