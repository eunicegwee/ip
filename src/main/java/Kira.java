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

        // Initialize scanner
        Scanner in = new Scanner(System.in);
        String command;

        while (true) {
            command = in.nextLine();

            if (command.equals("bye")) {
                break;
            }

            // Echo the command
            System.out.println(line);
            System.out.println(" " + command);
            System.out.println(line);
        }

        // Exit
        System.out.println(line);
        System.out.println(" Adios. See you later!");
        System.out.println(line);
    }
}
