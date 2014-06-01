package com.spacemadness.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.spacemadness.engine.commands.ChatMessage;
import com.spacemadness.engine.commands.Command;

public class Server implements CommandQueue {
	private static final ExecutorService threadpool = Executors.newFixedThreadPool(3);

	final int port;

	private List<Listener> clients = new ArrayList<>();

	private Server(int port) {
		this.port = port;
	}

	public void send(Command c) {
		for (Listener client : clients) {
			client.send(c);
		}
	}

	public static CommandQueue connectTo(String uri) {
		return null;
	}
	
	private class Listener implements Runnable, CommandQueue {
		private final Socket connection;
		private final ObjectInputStream objectInputStream;
		private final ObjectOutputStream objectOutputStream;

		public Listener(Socket connection) throws IOException {
			this.connection = connection;
			this.objectInputStream = new ObjectInputStream(connection.getInputStream());
			this.objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
		}

		@Override
		public void run() {
			try {
				while (true) {
					Object command = objectInputStream.readObject();
					if (command instanceof ChatMessage) {
						ChatMessage m = (ChatMessage) command;
						System.out.println("chat: " + m);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					connection.close();
				} catch (IOException ignore) {
				}
			}
		}

		@Override
		public void send(Command c) {
			try {
				objectOutputStream.writeObject(c);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void runServer() throws IOException {
		try (ServerSocket socket = new ServerSocket(port)) {
			while (true) {
				Socket newConnection = socket.accept();
				InetAddress inetAddress = newConnection.getInetAddress();

				System.out.println("new connection from: " + inetAddress);
				Listener client = new Listener(newConnection);
				clients.add(client);

				threadpool.submit(client);
			}
		}
	}

	public static Server create() {
		final Server s = new Server(6555);
		threadpool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					s.runServer();
				} catch (IOException e) {
				}
			}
		});
		return s;
	}
}
