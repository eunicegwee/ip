package kira;

import java.util.Scanner;

/**
 * Handles all user interactions, including reading input and printing messages to the console.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner in;

    public Ui() {
        this.in = new Scanner(System.in);
        assert in != null : "Scanner must be initialized";
    }

    /**
     * Reads the next line of input from the user.
     * @return The user's full command string.
     */
    public String readCommand() {
        String line = in.nextLine();
        assert line != null : "readCommand should not return null";
        return line;
    }

    /**
     * Displays the welcome message and logo.
     */
    public void showWelcome() {
        String logo = """
                    __ __  _           \s
                   / //_/ (_)_________ \s
                  / ,<   / / ___/ __ \\\s
                 / /| | / / /  / /_/ /\s
                /_/ |_|/_/_|   \\__,_/ \s
                """;
        System.out.println("Hello from\n" + logo);
        showLine();
        System.out.println(" Hello! I'm Kira.");
        System.out.println(" How can I help you today?");
        showLine();
    }

    /**
     * Prints a horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays an error message to the user.
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Displays an error when loading the file fails.
     */
    public void showLoadingError() {
        System.out.println(" No previous data found. Starting fresh task list!");
    }

    /**
     * Displays a generic message to the user.
     * @param message The message to display.
     */
    public void showMessage(String message) {
        System.out.println(" " + message);
    }

    /**
     * Close any resources used by Ui (scanner).
     * No-op for subclasses that do not use System.in.
     */
    public void close() {
        in.close();
    }
}

// End of Ui.java
