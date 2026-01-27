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
                String userInput = ui.readCommand();
                ui.showLine();

                Command command = parseCommand(userInput);

                switch (command) {
                    case BYE:
                        ui.showMessage(" Adios. See you later!");
                        isExit = true;
                        break;

                    case LIST:
                        ui.showMessage("Here are all your TASKS!");
                        for (int i = 0; i < tasks.size(); i++) {
                            ui.showMessage(" " + (i + 1) + "." + tasks.get(i).toString());
                        }
                        break;

                    case MARK:
                        int markIndex = getIndex(userInput, tasks);
                        tasks.get(markIndex).markAsDone();
                        storage.save(tasks);
                        ui.showMessage(" Yay! Task marked as done:");
                        ui.showMessage("   " + tasks.get(markIndex).toString());
                        break;

                    case UNMARK:
                        int unmarkIndex = getIndex(userInput, tasks);
                        tasks.get(unmarkIndex).markAsUndone();
                        storage.save(tasks);
                        ui.showMessage(" Okay... got it, not done yet:");
                        ui.showMessage("   " + tasks.get(unmarkIndex).toString());
                        break;

                    case DELETE:
                        int deleteIndex = getIndex(userInput, tasks);
                        Task removedTask = tasks.delete(deleteIndex);
                        storage.save(tasks);
                        ui.showMessage(" Okay, got it! REMOVED:");
                        ui.showMessage("   " + removedTask.toString());
                        ui.showMessage(" Now you have " + tasks.size() + " TASKS in the list!");
                        break;

                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        Task newTask = getTask(command, userInput);
                        tasks.add(newTask);
                        storage.save(tasks);
                        ui.showMessage(" YAY! Task added:");
                        ui.showMessage("   " + newTask);
                        ui.showMessage(" Now you have " + tasks.size() + " TASKS in the list!");
                        break;

                    case FILTER:
                        if (userInput.length() <= 7) {
                            throw new KiraException("Error: Please specify a date (e.g., filter 2025-01-30).");
                        }
                        String dateString = userInput.substring(7);
                        LocalDate targetDate = LocalDate.parse(dateString);

                        ui.showMessage(" DEADLINES AND/OR EVENTS HAPPENING ON " + targetDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM d yyyy")) + ":");

                        int count = 0;
                        for (Task t : tasks.getAll()) {
                            if (t instanceof Deadline) {
                                LocalDate dDate = ((Deadline) t).by.toLocalDate();
                                if (dDate.equals(targetDate)) {
                                    ui.showMessage("   " + t);
                                    count++;
                                }
                            } else if (t instanceof Event) {
                                LocalDate fromDate = ((Event) t).from.toLocalDate();
                                LocalDate toDate = ((Event) t).to.toLocalDate();

                                if (!targetDate.isBefore(fromDate) && !targetDate.isAfter(toDate)) {
                                    ui.showMessage("   " + t);
                                    count++;
                                }
                            }
                        }

                        if (count == 0) {
                            ui.showMessage("   (Seems like you're free that day!)");
                        }
                        break;

                    default:
                        throw new KiraException("Errr I'm afraid that's not a valid command...");
                }
            } catch (KiraException e) {
                ui.showError(" " + e.getMessage());
            } catch (NumberFormatException e) {
                ui.showError(" OOPS! Please enter a valid number.");
            } catch (DateTimeParseException e) {
                ui.showError(" OOPS! Please enter a valid date: use yyyy-MM-dd HH:mm (e.g., 2025-01-30 18:00)");
            }

            ui.showLine();
        }
    }

    public static void main(String[] args) {
        new Kira("data/kira.txt").run();
    }

    private int getIndex(String userInput, TaskList tasks) throws KiraException {
        String[] markParts = userInput.split(" ");
        if (markParts.length < 2) {
            throw new KiraException("OOPS! Please specify the task number.");
        }
        int markIndex = Integer.parseInt(markParts[1]) - 1;
        if (markIndex < 0 || markIndex >= tasks.size()) {
            throw new KiraException("OOPS! There doesn't seem to be a task " + markParts[1] + "!");
        }
        return markIndex;
    }

    private Command parseCommand(String userInput) {
        String firstWord = userInput.split(" ")[0].toUpperCase();
        try {
            return Command.valueOf(firstWord);
        } catch (IllegalArgumentException e) {
            return Command.UNKNOWN;
        }
    }

    private Task getTask(Command type, String command) throws KiraException {
        switch (type) {
            case TODO:
                if (command.length() <= 5) {
                    throw new KiraException("OOPS! Please provide a todo description!");
                }
                return new ToDo(command.substring(5));
            case DEADLINE:
                if (command.length() <= 9) {
                    throw new KiraException("OOPS! Please provide a deadline description!");
                }
                String[] dParts = command.substring(9).split(" /by ");
                if (dParts.length < 2) {
                    throw new KiraException("OOPS! Please add a deadline time (use /by).");
                }
                return new Deadline(dParts[0], dParts[1]);
            case EVENT:
                if (command.length() <= 6) {
                    throw new KiraException("OOPS! Please provide a deadline description!");
                }
                String[] eParts = command.substring(6).split(" /from ");
                if (eParts.length < 2) {
                    throw new KiraException("OOPS! Please add an event start time (use /from).");
                }
                String[] timeParts = eParts[1].split(" /to ");
                if (timeParts.length < 2) {
                    throw new KiraException("OOPS! Please add an event end time (use /to).");
                }
                return new Event(eParts[0], timeParts[0], timeParts[1]);
            default:
                throw new KiraException("Errr I'm afraid that's not a valid command...");
        }
    }
}
