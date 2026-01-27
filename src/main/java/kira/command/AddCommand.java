package kira.command;

import kira.Storage;
import kira.task.Task;
import kira.task.TaskList;
import kira.Ui;

public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks);
        ui.showMessage(" YAY! kira.task.Task added:");
        ui.showMessage("   " + task);
        ui.showMessage(" Now you have " + tasks.size() + " TASKS in the list!");
    }
}