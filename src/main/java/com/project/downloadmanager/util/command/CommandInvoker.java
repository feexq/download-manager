package com.project.downloadmanager.util.command;

import java.util.Stack;

public class CommandInvoker {
    private Stack<Command> commands = new Stack<>();

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        commands.push(command);
        command.execute();
    }

    public void undoCommand() {
        if (!commands.isEmpty()) {
            Command command = commands.pop();
            command.undo();
        }
    }
}
