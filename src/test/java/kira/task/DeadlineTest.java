package kira.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_validDate_success() {
        Deadline deadline = new Deadline("return book", "2025-01-30 18:00");
        assertEquals("[D][ ] return book (by: Jan 30 2025 18:00)", deadline.toString());
    }

    @Test
    public void toFileFormat_validDate_success() {
        Deadline deadline = new Deadline("return book", "2025-01-30 18:00");
        assertEquals("D | 0 | return book | 2025-01-30 18:00", deadline.toFileFormat());
    }

    @Test
    public void constructor_invalidDateFormat_throwsException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("return book", "Sunday"));
    }
}
