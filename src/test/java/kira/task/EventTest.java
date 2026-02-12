package kira.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventTest {

    @Test
    public void toString_validDates_success() {
        Event event = new Event("project meeting", "2025-02-15 14:00", "2025-02-15 16:00");
        assertEquals("[E][ ] project meeting (from: Feb 15 2025 14:00 to: Feb 15 2025 16:00)", event.toString());
    }

    @Test
    public void toFileFormat_validDates_success() {
        Event event = new Event("project meeting", "2025-02-15 14:00", "2025-02-15 16:00");
        assertEquals("E | 0 | project meeting | 2025-02-15 14:00 | 2025-02-15 16:00", event.toFileFormat());
    }
}