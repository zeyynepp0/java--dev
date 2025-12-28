/**
 * PROJECT CONFIGURATION & CONSTANTS
 * * Purpose: Centralizes all string literals and configuration settings for Maintainability.
 * Ensures exact compliance with PDF prompt messages [cite: 43-51].
 */
public class Constants {
    // Files
    public static final String OUTPUT_FILE_NAME = "results.txt";
    public static final String LOG_FILE_NAME = "app.log";


    public static final String CMD_CANCEL = "cancel";

    // PDF Required Prompts
    public static final String MSG_ENTER_DEPT = "Enter department information";
    public static final String MSG_ENTER_STUDENT = "Enter student information";
    public static final String MSG_ENTER_COURSE = "Enter course information";
    public static final String CMD_END = "end";

    // Error Messages (User Experience)
    public static final String ERR_EMPTY = ">> ERROR: This field cannot be empty. Please try again.";
    public static final String ERR_INVALID_NAME = ">> ERROR: Name contains invalid characters. Use letters only.";
    public static final String ERR_INVALID_NUMBER = ">> ERROR: Invalid input! Please enter a numeric value.";
    public static final String ERR_NEGATIVE = ">> ERROR: Value cannot be negative.";
    public static final String ERR_DATE_FMT = ">> ERROR: Invalid date format! Expected: 'dd.MM.yyyy' (e.g., 25.09.2000).";
    public static final String ERR_FUTURE_DATE = ">> ERROR: Date cannot be in the future.";
    public static final String ERR_DUPLICATE_ID = ">> CRITICAL ERROR: This Student ID is already registered! Duplicate prevented.";
    public static final String MSG_CONFIRM = ">> Is the information above correct? (y/n): ";
    public static final String MSG_CANCELLED = ">> Entry cancelled by user.";
    public static final String MSG_RETRY = ">> Reloading entry form...";
}