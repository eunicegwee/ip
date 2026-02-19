package kira.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kira.Ui;
import kira.task.TaskList;
import kira.task.ToDo;

public class FindCommandTest {

    private static class CapturingUi extends Ui {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showError(String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    private static String stripTags(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("<[^>]+>", "");
    }

    @Test
    public void execute_withMatches_showsMatchingTasks() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("write code"));
        tasks.add(new ToDo("book flight"));

        CapturingUi ui = new CapturingUi();
        FindCommand cmd = new FindCommand("book");
        cmd.execute(tasks, ui, null);

        assertFalse(ui.getMessages().isEmpty());
        String first = stripTags(ui.getMessages().get(0));
        assertEquals("Found these tasks matching \"book\" in your list:", first);

        boolean hasReadBook = ui.getMessages().stream().anyMatch(m -> m.contains("read book"));
        boolean hasBookFlight = ui.getMessages().stream().anyMatch(m -> m.contains("book flight"));
        assertTrue(hasReadBook);
        assertTrue(hasBookFlight);
    }

    @Test
    public void execute_noMatches_showsNoMatchesMessage() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        CapturingUi ui = new CapturingUi();
        FindCommand cmd = new FindCommand("xyz");
        cmd.execute(tasks, ui, null);

        assertTrue(ui.getMessages().stream().anyMatch(m -> m.contains("No matching tasks found")));
    }
}
