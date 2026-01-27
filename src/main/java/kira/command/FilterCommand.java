package kira.command;

import kira.Storage;
import kira.task.Task;
import kira.task.TaskList;
import kira.Ui;
import kira.task.Deadline;
import kira.task.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilterCommand extends Command {
    private final LocalDate targetDate;

    public FilterCommand(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        ui.showMessage(" DEADLINES AND/OR EVENTS HAPPENING ON " + formattedDate + ":");

        int count = 0;
        // Access the raw list using tasks.getAll()
        for (Task t : tasks.getAll()) {
            if (t instanceof Deadline) {
                LocalDate dDate = ((Deadline) t).getBy().toLocalDate();
                if (dDate.equals(targetDate)) {
                    ui.showMessage("   " + t);
                    count++;
                }
            } else if (t instanceof Event) {
                LocalDate fromDate = ((Event) t).getFrom().toLocalDate();
                LocalDate toDate = ((Event) t).getTo().toLocalDate();

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