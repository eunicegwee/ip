package kira.command;

import kira.KiraException;
import kira.Storage;
import kira.Ui;
import kira.task.Task;
import kira.task.TaskList;

/**
 * Marks or unmarks a task as done.
 */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isDone; // true for mark, false for unmark

    /**
     * Creates a new MarkCommand.
     * @param index index of task to mark/unmark
     * @param isDone true to mark, false to unmark
     */
    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    /**
     * Executes the mark/unmark action.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiraException {
        if (index < 0 || index >= tasks.size()) {
            throw new KiraException("OOPS! Invalid task number.");
        }

        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
            ui.showMessage(" Yay! Task marked as done:");
        } else {
            task.markAsUndone();
            ui.showMessage(" Okay... got it, not done yet:");
        }

        ui.showMessage("   " + task);
        storage.save(tasks);
    }
}

// End of MarkCommand.java
