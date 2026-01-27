package kira.command;

import kira.Storage;
import kira.task.TaskList;
import kira.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here are all your TASKS!");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage(" " + (i + 1) + "." + tasks.get(i).toString());
        }
    }
}