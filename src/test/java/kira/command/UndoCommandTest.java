package kira.command;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import kira.CommandResult;
import kira.Kira;
import kira.ResponseUi;

public class UndoCommandTest {

    @Test
    public void undoAfterAddRemovesTask() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        kira.handleCommand("todo write tests");
        CommandResult resList1 = kira.handleCommand("list");
        assertTrue(resList1.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("write tests")));

        kira.handleCommand("undo");
        CommandResult resList2 = kira.handleCommand("list");
        assertFalse(resList2.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("write tests")));
    }

    @Test
    public void undoWithNoHistoryShowsError() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        CommandResult res = kira.handleCommand("undo");
        List<String> msgs = res.getMessages();
        boolean hasError = msgs.stream().anyMatch(s -> s.toLowerCase().contains("nothing to undo")
                || s.toLowerCase().contains("nothing to undo."));
        assertTrue(hasError, "Undo with empty history should show an error");
    }

    @Test
    public void undoOnlyRevertsMostRecent() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        kira.handleCommand("todo first");
        kira.handleCommand("todo second");

        CommandResult listBefore = kira.handleCommand("list");
        assertTrue(listBefore.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("first")));
        assertTrue(listBefore.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("second")));

        kira.handleCommand("undo");

        CommandResult listAfter = kira.handleCommand("list");
        // second should be gone but first should remain
        assertTrue(listAfter.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("first")));
        assertFalse(listAfter.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("second")));
    }
}

