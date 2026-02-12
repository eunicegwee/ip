package kira;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import kira.command.AddCommand;
import kira.command.Command;
import kira.command.DeleteCommand;
import kira.command.ExitCommand;
import kira.command.FilterCommand;
import kira.command.FindCommand;
import kira.command.ListCommand;
import kira.command.MarkCommand;
import kira.command.UndoCommand;
import kira.task.Deadline;
import kira.task.Event;
import kira.task.ToDo;

/**
 * Parses user input into executable Command objects.
 */
public class Parser {

    /**
     * Parses the full user command and returns the corresponding Command object.
     * @param fullCommand The full string input by the user.
     * @return A Command object corresponding to the user's intent.
     * @throws KiraException If the command is unknown or the format is invalid.
     */
    public static Command parse(String fullCommand) throws KiraException {
        // Precondition
        assert fullCommand != null : "fullCommand must not be null";

        String[] parts = fullCommand.split(" ", 2); // Split command word and arguments
        String commandWord = parts[0].toLowerCase();

        if (commandWord.equals("bye")) {
            return new ExitCommand();
        } else if (commandWord.equals("list")) {
            return new ListCommand();
        } else if (commandWord.equals("undo")) {
            return new UndoCommand();
        } else if (commandWord.equals("mark")) {
            return new MarkCommand(parseIndex(parts), true);
        } else if (commandWord.equals("unmark")) {
            return new MarkCommand(parseIndex(parts), false);
        } else if (commandWord.equals("delete")) {
            return new DeleteCommand(parseIndex(parts));
        } else if (commandWord.equals("find")) {
            if (parts.length < 2) {
                throw new KiraException("OOPS! Please specify a search keyword.");
            }
            return new FindCommand(parts[1]);
        } else if (commandWord.equals("todo")) {
            if (parts.length < 2) {
                throw new KiraException("OOPS! The description of a todo cannot be empty.");
            }
            return new AddCommand(new ToDo(parts[1]));
        } else if (commandWord.equals("deadline")) {
            return prepareDeadline(parts);
        } else if (commandWord.equals("event")) {
            return prepareEvent(parts);
        } else if (commandWord.equals("filter")) {
            return prepareFilter(parts);
        } else {
            throw new KiraException("OOPS! I'm sorry, but I don't know what that means :-(");
        }
    }

    private static int parseIndex(String[] parts) throws KiraException {
        // Precondition
        assert parts != null : "parts must not be null";

        if (parts.length < 2) {
            throw new KiraException("OOPS! Please specify the task number.");
        }
        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new KiraException("OOPS! That is not a valid number.");
        }
    }

    private static Command prepareDeadline(String[] parts) throws KiraException {
        // Precondition
        assert parts != null : "parts must not be null";

        if (parts.length < 2) {
            throw new KiraException("OOPS! The description of a deadline cannot be empty.");
        }
        String[] dParts = parts[1].split(" /by ");
        if (dParts.length < 2) {
            throw new KiraException("OOPS! Please add a deadline time (use /by).");
        }
        try {
            return new AddCommand(new Deadline(dParts[0], dParts[1]));
        } catch (DateTimeParseException e) {
            throw new KiraException("OOPS! Invalid Date format. Use yyyy-MM-dd HH:mm");
        }
    }

    private static Command prepareEvent(String[] parts) throws KiraException {
        // Precondition
        assert parts != null : "parts must not be null";

        if (parts.length < 2) {
            throw new KiraException("OOPS! The description of an event cannot be empty.");
        }
        String[] eParts = parts[1].split(" /from ");
        if (eParts.length < 2) {
            throw new KiraException("OOPS! Please add a start time (use /from).");
        }
        String[] timeParts = eParts[1].split(" /to ");
        if (timeParts.length < 2) {
            throw new KiraException("OOPS! Please add an end time (use /to).");
        }
        try {
            return new AddCommand(new Event(eParts[0], timeParts[0], timeParts[1]));
        } catch (DateTimeParseException e) {
            throw new KiraException("OOPS! Invalid Date format. Use yyyy-MM-dd HH:mm");
        }
    }

    private static Command prepareFilter(String[] parts) throws KiraException {
        // Precondition
        assert parts != null : "parts must not be null";

        if (parts.length < 2) {
            throw new KiraException("OOPS! Please specify a date (e.g., filter 2025-01-30).");
        }
        try {
            LocalDate date = LocalDate.parse(parts[1]);
            return new FilterCommand(date);
        } catch (DateTimeParseException e) {
            throw new KiraException("OOPS! Invalid Date format. Please use yyyy-MM-dd");
        }
    }
}

// End of Parser.java

