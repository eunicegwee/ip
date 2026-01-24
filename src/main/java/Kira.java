import java.util.Scanner;
import java.util.ArrayList;

public class Kira {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String logo = """
                    __ __  _           \s
                   / //_/ (_)_________ \s
                  / ,<   / / ___/ __ \\\s
                 / /| | / / /  / /_/ /\s
                /_/ |_|/_/_|   \\__,_/ \s
                """;

        System.out.println("Hello from\n" + logo);

        // Greeting
        System.out.println(line);
        System.out.println(" Hello! I'm Kira!");
        System.out.println(" How can I help you today?");
        System.out.println(line);

        // Initialize scanner and tasks tracker
        Scanner in = new Scanner(System.in);
        String userInput;

        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            try {
                userInput = in.nextLine();
                System.out.println(line);
                Command command = parseCommand(userInput);

                switch (command) {
                    case BYE:
                        System.out.println(" Adios. See you later!");
                        System.out.println(line);
                        return;

                    case LIST:
                        System.out.println("Here are all your TASKS!");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println(" " + (i + 1) + "." + tasks.get(i).toString());
                        }
                        break;

                    case MARK:
                        int markIndex = getIndex(userInput, tasks);
                        tasks.get(markIndex).markAsDone();
                        System.out.println(" Yay! Task marked as done:");
                        System.out.println("   " + tasks.get(markIndex).toString());
                        break;

                    case UNMARK:
                        int unmarkIndex = getIndex(userInput, tasks);
                        tasks.get(unmarkIndex).markAsUndone();
                        System.out.println(" Okay... got it, not done yet:");
                        System.out.println("   " + tasks.get(unmarkIndex).toString());
                        break;

                    case DELETE:
                        int deleteIndex = getIndex(userInput, tasks);
                        Task removedTask = tasks.remove(deleteIndex);
                        System.out.println(" Okay, got it! REMOVED:");
                        System.out.println("   " + removedTask.toString());
                        System.out.println(" Now you have " + tasks.size() + " TASKS in the list!");
                        break;

                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        Task newTask = getTask(command, userInput);
                        tasks.add(newTask);

                        System.out.println(" YAY! Task added:");
                        System.out.println("   " + newTask);
                        System.out.println(" Now you have " + tasks.size() + " TASKS in the list!");
                        break;

                    default:
                        throw new KiraException("Errr I'm afraid that's not a valid command...");
                }
            } catch (KiraException e) {
                System.out.println(" " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println(" OOPS! Please enter a valid number.");
            }

            System.out.println(line);
        }
    }

    private static int getIndex(String userInput, ArrayList<Task> tasks) throws KiraException {
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

    private static Command parseCommand(String userInput) {
        String firstWord = userInput.split(" ")[0].toUpperCase();
        try {
            return Command.valueOf(firstWord);
        } catch (IllegalArgumentException e) {
            return Command.UNKNOWN;
        }
    }

    private static Task getTask(Command type, String command) throws KiraException {
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
