package kira.command;

import kira.KiraException;
import kira.Storage;
import kira.Ui;
import kira.task.Task;
import kira.task.TaskList;

/**
 * Finds tasks whose description contains the given keyword (case-insensitive).
 */
public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiraException {
        ui.showMessage("Here are the matching tasks in your list:");
        int shown = 0;
        int idx = 1;
        String lower = keyword.toLowerCase();
        for (Task t : tasks.getAll()) {
            if (t.toString().toLowerCase().contains(lower)) {
                ui.showMessage(" " + idx + "." + t.toString());
                idx++;
                shown++;
            }
        }
        if (shown == 0) {
            ui.showMessage("   (No matching tasks found.)");
        }
    }
}
