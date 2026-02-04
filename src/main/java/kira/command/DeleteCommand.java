package kira.command;

import kira.KiraException;
import kira.Storage;
import kira.Ui;
import kira.task.Task;
import kira.task.TaskList;

/**
 * Deletes a task from the task list by index.
 */
public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiraException {
        if (index < 0 || index >= tasks.size()) {
            throw new KiraException("OOPS! Invalid task number.");
        }
        Task removed = tasks.delete(index);
        storage.save(tasks);
        ui.showMessage(" Okay, got it! REMOVED:");
        ui.showMessage("   " + removed);
        ui.showMessage(" Now you have " + tasks.size() + " TASKS in the list!");
    }
}

// End of DeleteCommand.java
