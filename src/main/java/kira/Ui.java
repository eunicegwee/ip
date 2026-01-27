package kira;

import java.util.Scanner;

public class Ui {
    private final Scanner in;

    public Ui() {
        this.in = new Scanner(System.in);
    }

    public String readCommand() {
        return in.nextLine();
    }

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

    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    public void showError(String message) {
        System.out.println(" " + message);
    }

    public void showLoadingError() {
        System.out.println(" No previous data found. Starting fresh task list!");
    }

    public void showMessage(String message) {
        System.out.println(" " + message);
    }
}