package kira.command;

import kira.Storage;
import kira.Ui;
import kira.task.TaskList;

/**
 * Command to exit the application.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage(" Adios. See you later!");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}

// End of ExitCommand.java
