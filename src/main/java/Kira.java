import java.util.Scanner;
import java.util.ArrayList;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

public class Kira {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    public Kira(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (KiraException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();

                Command c = Parser.parse(fullCommand);

                c.execute(tasks, ui, storage);

                isExit = c.isExit();

            } catch (KiraException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Kira("data/kira.txt").run();
    }
}
