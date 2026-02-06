package kira;

import java.util.ArrayList;
import java.util.List;

/**
 * A Ui implementation that captures output into a list of lines for GUI use.
 */
public class ResponseUi extends Ui {
    private static final String LINE = "____________________________________________________________";
    private final List<String> lines = new ArrayList<>();

    @Override
    public void showWelcome() {
        lines.add(" Hello! I'm Kira.");
    }

    @Override
    public void showLine() {
        // For GUI, do not include divider lines; suppress them so dialogs are cleaner.
    }

    @Override
    public void showMessage(String message) {
        lines.add(message);
    }

    @Override
    public void showError(String message) {
        lines.add(message);
    }

    @Override
    public void showLoadingError() {
        lines.add(" No previous data found. Starting fresh task list!");
    }

    /**
     * Returns and clears the accumulated messages.
     */
    public List<String> drainMessages() {
        List<String> out = new ArrayList<>(lines);
        lines.clear();
        return out;
    }

    public boolean hasMessages() {
        return !lines.isEmpty();
    }
}

// End of ResponseUi.java
