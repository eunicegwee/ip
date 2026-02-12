package kira.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import kira.CommandResult;
import kira.Kira;
import kira.ResponseUi;

public class UndoDeadlineEventTest {

    @Test
    public void undoAfterAddDeadlineRemovesTask() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        kira.handleCommand("deadline submit report /by 2026-02-20 12:00");
        CommandResult resList1 = kira.handleCommand("list");
        assertTrue(resList1.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("submit report")));

        kira.handleCommand("undo");
        CommandResult resList2 = kira.handleCommand("list");
        assertFalse(resList2.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("submit report")));
    }

    @Test
    public void undoAfterAddEventRemovesTask() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        kira.handleCommand("event project meeting /from 2026-02-20 10:00 /to 2026-02-20 11:00");
        CommandResult resList1 = kira.handleCommand("list");
        assertTrue(resList1.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("project meeting")));

        kira.handleCommand("undo");
        CommandResult resList2 = kira.handleCommand("list");
        assertFalse(resList2.getMessages().stream().anyMatch(s -> s.toLowerCase().contains("project meeting")));
    }

    @Test
    public void undoRevertsMarkOnDeadline() throws Exception {
        Path tempDir = Files.createTempDirectory("kira-test-");
        String filePath = tempDir.resolve("kira.txt").toString();

        ResponseUi ui = new ResponseUi();
        Kira kira = new Kira(filePath, ui);

        kira.handleCommand("deadline pay bills /by 2026-02-25 09:00");
        // mark the deadline as done
        kira.handleCommand("mark 1");
        CommandResult afterMark = kira.handleCommand("list");
        boolean markedNow = afterMark.getMessages().stream()
                .anyMatch(s -> s.toLowerCase().contains("[x]")
                        && s.toLowerCase().contains("pay bills"));
        assertTrue(markedNow);

        // undo mark
        kira.handleCommand("undo");
        CommandResult afterUndo = kira.handleCommand("list");
        // should be not done (no [X])
        boolean marked = afterUndo.getMessages().stream()
                .anyMatch(s -> s.toLowerCase().contains("[x]")
                        && s.toLowerCase().contains("pay bills"));
        assertFalse(marked, "Undo should revert the mark on the deadline");
    }
}
