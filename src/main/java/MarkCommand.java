public class MarkCommand extends Command {
    private final int index;
    private final boolean isDone; // true for mark, false for unmark

    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

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