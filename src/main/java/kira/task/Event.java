package kira.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event with a start and end datetime.
 */
public class Event extends Task {

    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs an Event with the given description, start and end datetimes.
     *
     * @param description event description
     * @param from start datetime in format yyyy-MM-dd HH:mm
     * @param to end datetime in format yyyy-MM-dd HH:mm
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDateTime.parse(from, STORAGE_FORMAT);
        this.to = LocalDateTime.parse(to, STORAGE_FORMAT);
    }

    // package-private constructor for copying
    Event(String description, LocalDateTime from, LocalDateTime to, boolean isDone) {
        super(description);
        this.from = from;
        this.to = to;
        this.isDone = isDone;
    }

    /**
     * Returns the start datetime of the event.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the end datetime of the event.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    public String toFileFormat() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | "
                + from.format(STORAGE_FORMAT) + " | " + to.format(STORAGE_FORMAT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public Task copy() {
        return new Event(this.description, this.from, this.to, this.isDone);
    }
}

// End of Event.java
