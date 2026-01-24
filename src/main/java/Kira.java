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
        String command;

        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            try {
                command = in.nextLine();

                // COMMAND: bye
                if (command.equals("bye")) {
                    break;
                }

                // COMMAND: list
                else if (command.equals("list")) {
                    System.out.println(line);
                    System.out.println("Here are all your TASKS!");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i).toString());
                    }
                }

                // COMMAND: mark
                else if (command.startsWith("mark")) {
                    String[] parts = command.split(" ");
                    if (parts.length < 2) {
                        throw new KiraException("OOPS! Please specify the task number.");
                    }
                    int index = Integer.parseInt(parts[1]) - 1;
                    if (index < 0 || index >= tasks.size()) {
                        throw new KiraException("OOPS! There doesn't seem to be a task " + parts[1] + "!");
                    }
                    tasks.get(index).markAsDone();
                    System.out.println(line);
                    System.out.println(" Yay! Task marked as done:");
                    System.out.println("   " + tasks.get(index).toString());
                }

                // COMMAND: unmark
                else if (command.startsWith("unmark")) {
                    String[] parts = command.split(" ");
                    if (parts.length < 2) {
                        throw new KiraException("OOPS! Please specify the task number.");
                    }
                    int index = Integer.parseInt(parts[1]) - 1;
                    if (index < 0 || index >= tasks.size()) {
                        throw new KiraException("OOPS! There doesn't seem to be a task " + parts[1] + "!");
                    }
                    tasks.get(index).markAsUndone();
                    System.out.println(line);
                    System.out.println(" Okay... got it, not done yet:");
                    System.out.println("   " + tasks.get(index).toString());
                }

                // COMMAND: delete
                else if (command.startsWith("delete")) {
                    String[] parts = command.split(" ");
                    if (parts.length < 2) {
                        throw new KiraException("OOPS! Please specify the task number.");
                    }
                    int index = Integer.parseInt(parts[1]) - 1;
                    if (index < 0 || index >= tasks.size()) {
                        throw new KiraException("OOPS! There doesn't seem to be a task " + parts[1] + "!");
                    }
                    Task removedTask = tasks.remove(index);
                    System.out.println(line);
                    System.out.println(" Okay, got it! REMOVED:");
                    System.out.println("   " + removedTask.toString());
                    System.out.println(" Now you have " + tasks.size() + " TASKS in the list!");
                }

                // COMMAND: add task (todo/deadline/event)
                else {
                    Task newTask = getTask(command);
                    tasks.add(newTask);

                    System.out.println(line);
                    System.out.println(" YAY! Task added:");
                    System.out.println("   " + newTask);
                    System.out.println(" Now you have " + tasks.size() + " TASKS in the list!");
                }
            } catch (KiraException e) {
                System.out.println(" " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println(" OOPS! Please enter a valid number.");
            }

            System.out.println(line);
        }

        // Exit
        System.out.println(line);
        System.out.println(" Adios. See you later!");
        System.out.println(line);
    }

    private static Task getTask(String command) throws KiraException {
        if (command.startsWith("todo")) {
            if (command.length() <= 5) {
                throw new KiraException("OOPS! Please provide a todo description!");
            }
            String description = command.substring(5);
            return new ToDo(description);
        } else if (command.startsWith("deadline")) {
            if (command.length() <= 9) {
                throw new KiraException("OOPS! Please provide a deadline description!");
            }
            String[] parts = command.substring(9).split(" /by ");
            if (parts.length < 2) {
                throw new KiraException("OOPS! Please add a deadline time (use /by).");
            }
            return new Deadline(parts[0], parts[1]);
        } else if (command.startsWith("event")) {
            if (command.length() <= 6) {
                throw new KiraException("OOPS! Please provide a deadline description!");
            }
            String[] parts = command.substring(6).split(" /from ");
            if (parts.length < 2) {
                throw new KiraException("OOPS! Please add an event start time (use /from).");
            }
            String description = parts[0];
            String[] timeParts = parts[1].split(" /to ");
            if (timeParts.length < 2) {
                throw new KiraException("OOPS! Please add an event end time (use /to).");
            }
            return new Event(description, timeParts[0], timeParts[1]);
        } else {
            throw new KiraException("Errr I'm afraid that's not a valid command...");
        }
    }
}
