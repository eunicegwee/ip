package kira;

import kira.command.*;
import kira.task.Deadline;
import kira.task.Event;
import kira.task.ToDo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {

    public static Command parse(String fullCommand) throws KiraException {
        String[] parts = fullCommand.split(" ", 2); // Split command word and arguments
        String commandWord = parts[0].toLowerCase();

        return switch (commandWord) {
            case "bye" -> new ExitCommand();
            case "list" -> new ListCommand();
            case "mark" -> new MarkCommand(parseIndex(parts), true); // true = mark
            case "unmark" -> new MarkCommand(parseIndex(parts), false); // false = unmark
            case "delete" -> new DeleteCommand(parseIndex(parts));
            case "todo" -> {
                if (parts.length < 2) throw new KiraException("OOPS! The description of a todo cannot be empty.");
                yield new AddCommand(new ToDo(parts[1]));
            }
            case "deadline" -> prepareDeadline(parts);
            case "event" -> prepareEvent(parts);
            case "filter" -> prepareFilter(parts);
            default -> throw new KiraException("OOPS! I'm sorry, but I don't know what that means :-(");
        };
    }

    private static int parseIndex(String[] parts) throws KiraException {
        if (parts.length < 2) throw new KiraException("OOPS! Please specify the task number.");
        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new KiraException("OOPS! That is not a valid number.");
        }
    }

    private static Command prepareDeadline(String[] parts) throws KiraException {
        if (parts.length < 2) throw new KiraException("OOPS! The description of a deadline cannot be empty.");
        String[] dParts = parts[1].split(" /by ");
        if (dParts.length < 2) throw new KiraException("OOPS! Please add a deadline time (use /by).");
        try {
            return new AddCommand(new Deadline(dParts[0], dParts[1]));
        } catch (DateTimeParseException e) {
            throw new KiraException("OOPS! Invalid Date format. Use yyyy-MM-dd HH:mm");
        }
    }

    private static Command prepareEvent(String[] parts) throws KiraException {
        if (parts.length < 2) throw new KiraException("OOPS! The description of an event cannot be empty.");
        String[] eParts = parts[1].split(" /from ");
        if (eParts.length < 2) throw new KiraException("OOPS! Please add a start time (use /from).");
        String[] timeParts = eParts[1].split(" /to ");
        if (timeParts.length < 2) throw new KiraException("OOPS! Please add an end time (use /to).");
        try {
            return new AddCommand(new Event(eParts[0], timeParts[0], timeParts[1]));
        } catch (DateTimeParseException e) {
            throw new KiraException("OOPS! Invalid Date format. Use yyyy-MM-dd HH:mm");
        }
    }

    private static Command prepareFilter(String[] parts) throws KiraException {
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