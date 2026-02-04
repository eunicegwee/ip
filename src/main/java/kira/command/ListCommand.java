package kira.command;

import kira.Storage;
import kira.Ui;
import kira.task.TaskList;

/**
 * Lists all tasks to the user.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here are all your TASKS!");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage(" " + (i + 1) + "." + tasks.get(i).toString());
        }
    }
}

// End of ListCommand.java
