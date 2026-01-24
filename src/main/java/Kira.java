import java.util.Scanner;

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

        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (true) {
            command = in.nextLine();

            if (command.equals("bye")) {
                break;
            }

            // COMMAND: list
            else if (command.equals("list")) {
                System.out.println(line);
                System.out.println("Here are all your TASKS!");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i].toString());
                }
                System.out.println(line);
            }

            // COMMAND: mark
            else if (command.startsWith("mark")) {
                String[] parts = command.split(" ");
                int index = Integer.parseInt(parts[1]) - 1;

                tasks[index].markAsDone();

                System.out.println(line);
                System.out.println(" Yay! Task marked as done:");
                System.out.println("   " + tasks[index].toString());
                System.out.println(line);
            }

            // COMMAND: unmark

            else if (command.startsWith("unmark")) {
                String[] parts = command.split(" ");
                int index = Integer.parseInt(parts[1]) - 1;

                tasks[index].markAsUndone();

                System.out.println(line);
                System.out.println(" Okay... got it, not done yet:");
                System.out.println("   " + tasks[index].toString());
                System.out.println(line);
            }

            else {
                tasks[taskCount] = new Task(command);
                taskCount++;

                System.out.println(line);
                System.out.println(" added: " + command);
                System.out.println(line);
            }
        }

        // Exit
        System.out.println(line);
        System.out.println(" Adios. See you later!");
        System.out.println(line);
    }
}
