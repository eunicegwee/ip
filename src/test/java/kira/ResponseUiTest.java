package kira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ResponseUiTest {

    @Test
    public void messages_accumulate_drain() {
        ResponseUi ui = new ResponseUi();
        ui.showWelcome();
        ui.showMessage("hello");
        ui.showError("bad");
        ui.showLoadingError();

        assertTrue(ui.hasMessages());
        List<String> drained = ui.drainMessages();
        assertEquals(4, drained.size());
        // error line should be prefixed
        assertTrue(drained.get(2).startsWith(ResponseUi.ERROR_PREFIX));
        // draining clears messages
        assertFalse(ui.hasMessages());
    }
}
