public class Kira {
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        String logo = """
                    __ __  _           \s
                   / //_/ (_)_________ \s
                  / ,<   / / ___/ __ \\\s
                 / /| | / / /  / /_/ /\s
                /_/ |_|/_/_|   \\__,_/ \s
                """;

        System.out.println("Hello from\n" + logo);

        // Greeting
        System.out.println(horizontalLine);
        System.out.println(" Hello! I'm Kira!");
        System.out.println(" How can I help you today?");
        System.out.println(horizontalLine);

        // Exit
        System.out.println(" Adios. See you later!");
        System.out.println(horizontalLine);
    }
}
