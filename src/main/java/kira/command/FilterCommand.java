package kira.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import kira.Storage;
import kira.Ui;
import kira.task.Deadline;
import kira.task.Event;
import kira.task.Task;
import kira.task.TaskList;

/**
 * Filters tasks by a given date and displays matching deadlines/events.
 */
public class FilterCommand extends Command {
    private final LocalDate targetDate;

    public FilterCommand(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    /**
     * Executes the filter command, displaying tasks that match the target date.
     *
     * @param tasks   The list of tasks to filter through.
     * @param ui      The user interface to display messages.
     * @param storage The storage handler (not used in this command).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        ui.showMessage(" DEADLINES AND/OR EVENTS HAPPENING ON " + formattedDate + ":");

        int count = 0;
        // Access the raw list using tasks.getAll()
        for (Task t : tasks.getAll()) {
            if (t instanceof Deadline) {
                java.time.LocalDate dDate = ((Deadline) t).getBy().toLocalDate();
                if (dDate.equals(targetDate)) {
                    ui.showMessage("   " + t);
                    count++;
                }
            } else if (t instanceof Event) {
                java.time.LocalDate fromDate = ((Event) t).getFrom().toLocalDate();
                java.time.LocalDate toDate = ((Event) t).getTo().toLocalDate();

                if (!targetDate.isBefore(fromDate) && !targetDate.isAfter(toDate)) {
                    ui.showMessage("   " + t);
                    count++;
                }
            }
        }

        if (count == 0) {
            ui.showMessage("   (Seems like you're free that day!)");
        }
    }
}

// End of FilterCommand.java

