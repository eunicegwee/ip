package kira;

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
     * Constructs a new Kira instance.
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
     * Runs the main program loop.
     * Continuously reads user commands, parses them, and executes them until the exit command is issued.
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
