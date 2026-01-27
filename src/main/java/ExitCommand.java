public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage(" Adios. See you later!");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}