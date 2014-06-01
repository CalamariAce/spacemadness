package com.spacemadness.engine.commands;


public class ChatMessage implements Command {

	private static final long serialVersionUID = 1L;

	private final String message;
	
	public ChatMessage(String msg) {
		this.message = msg;
	}

	@Override
	public String toString() {
		return message;
	}
}
