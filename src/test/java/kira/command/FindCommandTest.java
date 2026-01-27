package kira.command;

import kira.Storage;
import kira.Ui;
import kira.task.TaskList;
import kira.task.ToDo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FindCommandTest {

    private static class CapturingUi extends Ui {
        List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showError(String message) {
            messages.add(message);
        }
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

        assertFalse(ui.messages.isEmpty());
        assertEquals("Here are the matching tasks in your list:", ui.messages.get(0));

        boolean hasReadBook = ui.messages.stream().anyMatch(m -> m.contains("read book"));
        boolean hasBookFlight = ui.messages.stream().anyMatch(m -> m.contains("book flight"));
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

        assertTrue(ui.messages.stream().anyMatch(m -> m.contains("No matching tasks found")));
    }
}
