package kira.command;

import kira.Storage;
import kira.Ui;
import kira.task.Task;
import kira.task.TaskList;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks);
        ui.showMessage(" YAY! Task added:");
        ui.showMessage("   " + task);
        ui.showMessage(" Now you have " + tasks.size() + " TASKS in the list!");
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}

// End of AddCommand.java
