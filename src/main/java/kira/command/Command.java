package kira.command;

import kira.KiraException;
import kira.Storage;
import kira.Ui;
import kira.task.TaskList;

/**
 * Abstract base for all commands.
 */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KiraException;

    public boolean isExit() {
        return false;
    }
}

// End of Command.java
