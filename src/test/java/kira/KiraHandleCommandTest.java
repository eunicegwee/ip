package kira;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class KiraHandleCommandTest {

    @Test
    public void testAddAndList() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        // Add a todo
        CommandResult res1 = kira.handleCommand("todo read book");
        List<String> msgs1 = res1.getMessages();
        assertFalse(msgs1.isEmpty(), "Expected messages after adding a todo");
        boolean containsAdd = false;
        for (String s : msgs1) {
            String lower = s.toLowerCase();
            if (lower.contains("task added")
                || lower.contains("yay")
                || lower.contains("task added:")) {
                containsAdd = true;
                break;
            }
        }
        assertTrue(containsAdd, "Add command should report task added");

        // List tasks
        CommandResult res2 = kira.handleCommand("list");
        List<String> msgs2 = res2.getMessages();
        assertFalse(msgs2.isEmpty(), "Expected list output");
        boolean containsTask = msgs2.stream().anyMatch(s -> s.toLowerCase().contains("read book"));
        assertTrue(containsTask, "List should contain the added todo");
    }

    @Test
    public void testExitCommand() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        CommandResult res = kira.handleCommand("bye");
        assertTrue(res.isExit(), "bye command should set exit flag");
        assertFalse(res.getMessages().isEmpty(), "Expected exit message");
    }
}
