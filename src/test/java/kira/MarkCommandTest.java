package kira;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import kira.command.MarkCommand;
import kira.task.TaskList;
import kira.task.ToDo;

public class MarkCommandTest {

    @Test
    public void mark_unmark_invalidIndex() throws Exception {
        Path dir = Files.createTempDirectory("kira-mark-test-");
        String file = dir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Storage storage = new Storage(file);
        TaskList list = new TaskList();
        list.add(new ToDo("read"));

        // mark
        MarkCommand mark = new MarkCommand(0, true);
        mark.execute(list, ui, storage);
        assertTrue(list.get(0).toString().contains("X"));
        // unmark
        MarkCommand un = new MarkCommand(0, false);
        un.execute(list, ui, storage);
        assertTrue(list.get(0).toString().contains(" "));

        // invalid index throws
        MarkCommand bad = new MarkCommand(5, true);
        Exception e = assertThrows(KiraException.class, () -> bad.execute(list, ui, storage));
        assertTrue(e.getMessage().toLowerCase().contains("invalid task"));
    }
}
