package kira.command;

import kira.KiraException;
import kira.Storage;
import kira.Ui;
import kira.task.TaskList;

/**
 * Marker command that triggers an undo operation handled by Kira.
 */
public class UndoCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiraException {
        // Actual undo is handled by Kira.handleCommand (it inspects this command).
        // Do nothing here to avoid double-handling.
    }
}
