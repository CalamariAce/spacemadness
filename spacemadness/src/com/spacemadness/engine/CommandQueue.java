package com.spacemadness.engine;

import com.spacemadness.engine.commands.Command;

public interface CommandQueue {

	public void send(Command c);
}
