package kira.gui;

import java.util.List;
import java.util.StringJoiner;

import kira.CommandResult;
import kira.Kira;

/**
 * Adapter that allows the GUI to talk to the Kira backend.
 *
 * <p>Provides methods that return {@link CommandResult} so the GUI can display structured
 * output and react to the exit flag. When no backend is set, a simple echo response is used.
 */
public class KiraResponder {

    private Kira kiraBackend;

    public KiraResponder() {
        this.kiraBackend = null;
    }

    /**
     * Creates a responder bound to a Kira backend.
     *
     * @param kira the Kira backend to delegate to
     */
    public KiraResponder(Kira kira) {
        this.kiraBackend = kira;
    }

    /**
     * Injects the Kira backend instance after construction.
     *
     * @param kira the Kira backend to use
     */
    public void setKiraBackend(Kira kira) {
        this.kiraBackend = kira;
    }

    /**
     * Generates a single string response by joining the backend-provided message lines.
     *
     * @param input the user input
     * @return the joined response string
     */
    public String getResponse(String input) {
        if (kiraBackend != null) {
            CommandResult res = kiraBackend.handleCommand(input);
            List<String> msgs = res.getMessages();
            StringJoiner joiner = new StringJoiner(System.lineSeparator());
            for (String m : msgs) {
                joiner.add(m);
            }
            return joiner.toString();
        }
        return "Kira heard: " + input;
    }

    /**
     * Executes a command against the backend and returns the structured {@link CommandResult}.
     *
     * @param input user command string
     * @return the command result including message lines and exit flag
     */
    public CommandResult executeCommand(String input) {
        if (kiraBackend != null) {
            return kiraBackend.handleCommand(input);
        }
        return new CommandResult(false, "Kira heard: " + input);
    }

    /**
     * Requests the backend's welcome messages plus a commands block to be shown on GUI startup.
     * The returned CommandResult.messages contains the welcome lines followed by a single
     * multi-line string listing supported commands.
     *
     * @return the welcome + commands as a CommandResult
     */
    public CommandResult handleWelcome() {
        if (kiraBackend != null) {
            CommandResult welcomeRes = kiraBackend.handleWelcome();
            java.util.List<String> welcomeMsgs = welcomeRes.getMessages();
            java.util.List<String> commands = kiraBackend.getSupportedCommands();
            // Join commands into single string block
            StringJoiner joiner = new StringJoiner(System.lineSeparator());
            for (String c : commands) {
                joiner.add(c);
            }
            java.util.List<String> result = new java.util.ArrayList<>();
            if (welcomeMsgs != null && !welcomeMsgs.isEmpty()) {
                // keep first welcome messages as separate entries
                result.addAll(welcomeMsgs);
            }
            result.add(joiner.toString());
            return new CommandResult(result, false);
        }
        return new CommandResult(false, " Hello! I'm Kira.", "Supported commands: ...");
    }
}

// End of KiraResponder.java
