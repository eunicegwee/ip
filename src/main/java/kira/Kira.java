package kira;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import kira.command.Command;
import kira.command.UndoCommand;
import kira.task.TaskList;

/**
 * The main entry point of the Kira chatbot application.
 * Initializes the UI, Storage, and TaskList components and runs the main command loop.
 */
public class Kira {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    // Stack of previous TaskList snapshots for undo (most recent on top)
    private final Deque<TaskList> undoStack = new ArrayDeque<>();

    /**
     * Constructs a new Kira instance using the console UI.
     * @param filePath The file path where tasks are stored and retrieved.
     */
    public Kira(String filePath) {
        // Precondition: filePath should not be null
        assert filePath != null : "filePath must not be null";

        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (KiraException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        // Invariant: tasks must be initialized after constructor
        assert tasks != null : "tasks must not be null after load";
    }

    /**
     * Constructs a new Kira instance with a supplied Ui implementation (useful for GUI testing).
     * @param filePath location of storage file
     * @param ui the Ui implementation to receive output (e.g., ResponseUi for GUI)
     */
    public Kira(String filePath, Ui ui) {
        // Preconditions: both filePath and ui should not be null
        assert filePath != null : "filePath must not be null";
        assert ui != null : "ui must not be null";

        this.ui = ui;
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (KiraException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        // Invariant: tasks must be initialized after constructor
        assert tasks != null : "tasks must not be null after load";
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
        // Preconditions
        assert input != null : "input must not be null";
        assert ui != null : "ui must be non-null";

        boolean isExit = false;
        try {
            ui.showLine();
            Command c = Parser.parse(input);
            // Parser should not return null for recognized input
            assert c != null : "parsed command must not be null";

            // If the command is undo, handle it here by popping the stack
            if (c instanceof UndoCommand) {
                if (undoStack.isEmpty()) {
                    ui.showError("Nothing to undo.");
                } else {
                    TaskList previous = undoStack.pop();
                    tasks.replaceWith(previous);
                    storage.save(tasks);
                    ui.showMessage("Okay, undone last action.");
                }
            } else {
                // For other commands, snapshot if undoable
                if (c.isUndoable()) {
                    undoStack.push(tasks.copy());
                }
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            }
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
        assert ui != null : "ui must be non-null";

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
        cmds.add("  undo");

        // Ensure we actually provide command descriptions
        assert cmds != null && !cmds.isEmpty() : "supported commands must be present";
        return cmds;
    }

    /**
     * Runs the main program loop (console mode).
     */
    public void run() {
        // Basic invariants before entering the loop
        assert ui != null : "ui must be non-null";
        assert storage != null : "storage must be non-null";
        assert tasks != null : "tasks must be non-null";

        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                // readCommand should not return null in normal operation
                assert fullCommand != null : "readCommand must not return null";
                ui.showLine();

                Command c = Parser.parse(fullCommand);

                // If the command is undo, handle it here by popping the stack
                if (c instanceof UndoCommand) {
                    if (undoStack.isEmpty()) {
                        ui.showError("Nothing to undo.");
                    } else {
                        TaskList previous = undoStack.pop();
                        tasks.replaceWith(previous);
                        storage.save(tasks);
                        ui.showMessage("Okay, undone last action.");
                    }
                } else {
                    if (c.isUndoable()) {
                        undoStack.push(tasks.copy());
                    }
                    c.execute(tasks, ui, storage);
                    isExit = c.isExit();
                }

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
