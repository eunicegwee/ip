package kira.command;

import kira.KiraException;
import kira.Storage;
import kira.task.TaskList;
import kira.Ui;

public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KiraException;

    public boolean isExit() {
        return false;
    }
}