package kira.task;

import java.util.ArrayList;

/**
 * A simple container for storing tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        // Defensive copy to avoid retaining external references
        this.tasks = new ArrayList<>(tasks == null ? new ArrayList<>() : tasks);
    }

    public void add(Task t) {
        tasks.add(t);
    }

    public Task delete(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public ArrayList<Task> getAll() {
        return new ArrayList<>(tasks);
    }
}

// End of TaskList.java
