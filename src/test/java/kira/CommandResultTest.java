package kira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CommandResultTest {

    @Test
    public void constructor_getters_work() {
        CommandResult r = new CommandResult(true, "one", "two");
        List<String> msgs = r.getMessages();
        assertEquals(2, msgs.size());
        assertTrue(r.isExit());

        CommandResult empty = new CommandResult(false, (String[]) null);
        assertFalse(empty.isExit());
        assertNotNull(empty.getMessages());
        assertEquals(0, empty.getMessages().size());
    }
}
