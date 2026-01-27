package kira.task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TaskListTest {

    @Test
    public void add_newTask_sizeIncreases() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void delete_existingTask_sizeDecreases() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));

        Task deleted = tasks.delete(0);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] task 1", deleted.toString());
        assertEquals("[T][ ] task 2", tasks.get(0).toString()); // Remaining task shifts up
    }

    @Test
    public void get_validIndex_returnsCorrectTask() {
        TaskList tasks = new TaskList();
        Task t1 = new ToDo("test task");
        tasks.add(t1);
        assertSame(t1, tasks.get(0));
    }
}