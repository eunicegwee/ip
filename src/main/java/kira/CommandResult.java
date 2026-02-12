package kira;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates a response from the Kira backend as a list of message lines.
 */
public class CommandResult {
    private final List<String> messages;
    private final boolean isExit;

    /**
     * Constructs a CommandResult containing the backend messages and an exit flag.
     *
     * @param messages the list of message lines produced by the backend
     * @param isExit whether this command indicates the application should exit
     */
    public CommandResult(List<String> messages, boolean isExit) {
        assert messages != null : "messages must not be null";
        this.messages = messages;
        this.isExit = isExit;
    }

    /**
     * Convenience constructor allowing creation from varargs of message strings.
     *
     * @param isExit whether this command indicates the application should exit
     * @param messages message lines to include in the result
     */
    public CommandResult(boolean isExit, String... messages) {
        this.messages = new ArrayList<>();
        if (messages != null) {
            this.messages.addAll(Arrays.asList(messages));
        }
        this.isExit = isExit;
    }

    public List<String> getMessages() {
        return messages;
    }

    public boolean isExit() {
        return isExit;
    }
}

// End of CommandResult.java
