package kira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kira.task.Deadline;
import kira.task.Event;
import kira.task.Task;
import kira.task.TaskList;
import kira.task.ToDo;

public class StorageTest {

    @Test
    public void load_validFile_parsesTasks() throws Exception {
        Path dir = Files.createTempDirectory("kira-storage-test-");
        Path file = dir.resolve("kira.txt");

        StringBuilder sb = new StringBuilder();
        sb.append("T | 0 | read book\n");
        sb.append("D | 1 | return book | 2025-01-01 18:00\n");
        sb.append("E | 0 | meeting | 2025-01-01 18:00 | 2025-01-01 19:00\n");

        Files.writeString(file, sb.toString());

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size(), "Should have loaded three tasks");
        assertTrue(tasks.get(0) instanceof ToDo, "First should be ToDo");
        assertTrue(tasks.get(1) instanceof Deadline, "Second should be Deadline");
        assertTrue(tasks.get(2) instanceof Event, "Third should be Event");

        ToDo todo = (ToDo) tasks.get(0);
        assertEquals("[T][ ] read book", todo.toString());

        Deadline dl = (Deadline) tasks.get(1);
        // toString contains description and formatted by; check description present and status X
        assertTrue(dl.toString().toLowerCase().contains("return book"), "Deadline should contain description");
        assertTrue(dl.getBy() != null, "Deadline should have a datetime");

        Event ev = (Event) tasks.get(2);
        assertTrue(ev.toString().toLowerCase().contains("meeting"), "Event should contain description");
        assertNotNull(ev.getFrom());
        assertNotNull(ev.getTo());
    }

    @Test
    public void save_writesFile_inCorrectFormat() throws Exception {
        Path dir = Files.createTempDirectory("kira-storage-save-test-");
        Path file = dir.resolve("out.txt");

        TaskList list = new TaskList();
        list.add(new ToDo("read book"));
        list.add(new Deadline("return book", "2025-01-01 18:00"));
        list.add(new Event("meeting", "2025-01-01 18:00", "2025-01-01 19:00"));

        Storage storage = new Storage(file.toString());
        storage.save(list);

        List<String> lines = Files.readAllLines(file);
        assertEquals(3, lines.size(), "Should have three saved lines");
        // Each line should match the toFileFormat of tasks in order
        assertTrue(lines.get(0).startsWith("T | 0 | read book"));
        assertTrue(lines.get(1).startsWith("D | 0 | return book"));
        assertTrue(lines.get(2).startsWith("E | 0 | meeting"));
    }

}
