package kira.task;

/**
 * Represents a simple to-do task.
 */
public class ToDo extends Task {

    public ToDo(String description) {
        super(description);
    }

    // package-private constructor for copying
    ToDo(String description, boolean isDone) {
        super(description);
        this.isDone = isDone;
    }

    @Override
    public String toFileFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public Task copy() {
        return new ToDo(this.description, this.isDone);
    }
}
