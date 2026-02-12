package kira.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a deadline datetime.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");

    private final LocalDateTime by;

    /**
     * Constructs a Deadline with the given description and deadline datetime.
     *
     * @param description the task description
     * @param by the deadline datetime in format yyyy-MM-dd HH:mm
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, STORAGE_FORMAT);
    }

    // package-private constructor for copying
    Deadline(String description, LocalDateTime by, boolean isDone) {
        super(description);
        this.by = by;
        this.isDone = isDone;
    }

    /**
     * Returns the deadline datetime.
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | "
                + by.format(STORAGE_FORMAT);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public Task copy() {
        return new Deadline(this.description, this.by, this.isDone);
    }
}

// End of Deadline.java
