package kira;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import kira.task.TaskList;
import kira.task.ToDo;

public class TaskListCopyReplaceTest {

    @Test
    public void copy_replace_deepCopies() {
        TaskList t1 = new TaskList();
        t1.add(new ToDo("a"));
        t1.add(new ToDo("b"));

        TaskList copy = t1.copy();
        assertEquals(2, copy.size());
        // modify original
        t1.get(0).markAsDone();
        // copied should not reflect change
        assertEquals(" ", copy.get(0).getStatusIcon());

        // replaceWith
        TaskList other = new TaskList();
        other.add(new ToDo("x"));
        t1.replaceWith(other);
        assertEquals(1, t1.size());
        // after replacing with 'other', first task should be 'x'
        String desc = t1.get(0).toString().split(" ", 3)[2];
        assertEquals("x", desc);
    }
}
