package kira.task;

import java.util.ArrayList;

/**
 * A simple container for storing tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
        assert tasks != null : "tasks list must be initialized";
    }

    /**
     * Constructs a TaskList wrapping an existing list of tasks.
     *
     * @param tasks existing tasks to wrap (must not be null)
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "tasks parameter must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param t the task to add (must not be null)
     */
    public void add(Task t) {
        assert t != null : "task to add must not be null";
        tasks.add(t);
    }

    /**
     * Deletes the task at the given index and returns it.
     *
     * @param index index of the task to delete (0-based)
     * @return the removed task
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "delete index out of bounds";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index index of the task to retrieve (0-based)
     * @return the task at the index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "get index out of bounds";
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public ArrayList<Task> getAll() {
        assert tasks != null : "tasks list must not be null";
        return tasks;
    }
}

// End of TaskList.java
