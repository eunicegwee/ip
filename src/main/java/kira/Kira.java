package kira;

import java.util.ArrayList;
import java.util.List;

import kira.command.Command;
import kira.task.TaskList;

/**
 * The main entry point of the Kira chatbot application.
 * Initializes the UI, Storage, and TaskList components and runs the main command loop.
 */
public class Kira {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Constructs a new Kira instance using the console UI.
     * @param filePath The file path where tasks are stored and retrieved.
     */
    public Kira(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (KiraException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Constructs a new Kira instance with a supplied Ui implementation (useful for GUI testing).
     * @param filePath location of storage file
     * @param ui the Ui implementation to receive output (e.g., ResponseUi for GUI)
     */
    public Kira(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (KiraException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Processes a single command string and returns the generated output plus an exit flag.
     *
     * <p>When used with a {@link ResponseUi}, the Ui will capture messages produced by the
     * command execution. The returned {@link CommandResult} contains the captured lines as
     * a list and a boolean indicating whether the executed command triggers application exit.
     *
     * @param input the raw user command
     * @return a {@link CommandResult} with message lines and exit flag
     */
    public CommandResult handleCommand(String input) {
        boolean isExit = false;
        try {
            ui.showLine();
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
            isExit = c.isExit();
        } catch (KiraException e) {
            ui.showError(e.getMessage());
        } finally {
            ui.showLine();
        }

        List<String> outputLines = new ArrayList<>();
        if (ui instanceof ResponseUi) {
            outputLines = ((ResponseUi) ui).drainMessages();
        }
        return new CommandResult(outputLines, isExit);
    }

    /**
     * Returns a structured welcome message using the injected Ui.
     *
     * <p>This method is intended for GUI usage: it invokes the Ui welcome routine and
     * returns any messages produced as a {@link CommandResult} (messages list, no exit).
     *
     * @return welcome messages as a {@link CommandResult}
     */
    public CommandResult handleWelcome() {
        ui.showWelcome();
        List<String> outputLines = new ArrayList<>();
        if (ui instanceof ResponseUi) {
            outputLines = ((ResponseUi) ui).drainMessages();
        }
        return new CommandResult(outputLines, false);
    }

    /**
     * Returns a list of supported command descriptions suitable for display in the GUI.
     *
     * @return list of short help lines describing supported commands
     */
    public List<String> getSupportedCommands() {
        List<String> cmds = new ArrayList<>();
        cmds.add("Supported commands:");
        cmds.add("  todo <description>");
        cmds.add("  deadline <description> /by <yyyy-MM-dd HH:mm>");
        cmds.add("  event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>");
        cmds.add("  list");
        cmds.add("  mark <task number>");
        cmds.add("  unmark <task number>");
        cmds.add("  delete <task number>");
        cmds.add("  find <keyword>");
        cmds.add("  filter <yyyy-MM-dd>");
        cmds.add("  bye");
        return cmds;
    }

    /**
     * Runs the main program loop (console mode).
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();

                Command c = Parser.parse(fullCommand);

                c.execute(tasks, ui, storage);

                isExit = c.isExit();

            } catch (KiraException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * The main method that starts the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Kira("data/kira.txt").run();
    }
}
