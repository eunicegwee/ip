package kira;

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
        this.messages = messages;
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
