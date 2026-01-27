package kira;

import kira.command.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test
    public void parse_byeCommand_returnsExitCommand() throws Exception {
        assertTrue(Parser.parse("bye") instanceof ExitCommand);
    }

    @Test
    public void parse_todoCommand_returnsAddCommand() throws Exception {
        assertTrue(Parser.parse("todo read book") instanceof AddCommand);
    }

    @Test
    public void parse_invalidCommand_throwsException() {
        Exception exception = assertThrows(KiraException.class, () -> {
            Parser.parse("blah");
        });
        assertEquals("OOPS! I'm sorry, but I don't know what that means :-(", exception.getMessage());
    }

    @Test
    public void parse_missingDescription_throwsException() {
        Exception exception = assertThrows(KiraException.class, () -> {
            Parser.parse("todo");
        });
        assertEquals("OOPS! The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_deadline_returnsAddCommand() throws Exception {
        // Test full command parsing
        Command c = Parser.parse("deadline return book /by 2025-01-01 18:00");
        assertTrue(c instanceof AddCommand);
    }

    @Test
    public void parse_deadlineMissingBy_throwsException() {
        Exception e = assertThrows(KiraException.class, () -> {
            Parser.parse("deadline return book");
        });
        assertEquals("OOPS! Please add a deadline time (use /by).", e.getMessage());
    }

    @Test
    public void parse_eventInvalidFormat_throwsException() {
        Exception e = assertThrows(KiraException.class, () -> {
            Parser.parse("event meeting /from 2025-01-01 18:00");
        });
        assertEquals("OOPS! Please add an end time (use /to).", e.getMessage());
    }

    @Test
    public void parse_deleteValidIndex_returnsDeleteCommand() throws Exception {
        Command c = Parser.parse("delete 1");
        assertTrue(c instanceof DeleteCommand);
    }

    @Test
    public void parse_deleteInvalidIndex_throwsException() {
        Exception e = assertThrows(KiraException.class, () -> {
            Parser.parse("delete abc");
        });
        assertEquals("OOPS! That is not a valid number.", e.getMessage());
    }
}