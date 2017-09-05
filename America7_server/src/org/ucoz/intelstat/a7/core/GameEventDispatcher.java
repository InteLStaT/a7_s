package org.ucoz.intelstat.a7.core;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class GameEventDispatcher extends Thread {

	private BlockingQueue<Runnable> events = new ArrayBlockingQueue<>(128);

	public GameEventDispatcher() {
		setDaemon(true);
	}
	
	public void run() {
		while (true) {
			try {
				events.take().run();
			} catch (InterruptedException e) {
				// I hate interrupted exception. Why does it have to
				// make my precious code look bad. Why is it checked.
				// That makes no sense. Thank you Compiler for making
				// interrupted exception not hear this.
				System.err.println("Something interrupted GED");
			}
		}
	}

	public void postEvent(List<GameListener> listeners, Consumer<GameListener> consumer) {
		try {
			events.put(() -> {
				for (GameListener gl : listeners) {
					consumer.accept(gl);
				}
			});
		} catch (InterruptedException e) {
			System.err.println("Something interrupted GED.postEvent");
		}
	}

	public void invokeLater(Runnable r) {
		try {
			events.put(r);
			System.err.println("  \033[31m+Runnable dispatched\033[0m");
		} catch (InterruptedException e) {
			System.err.println("Something interrupted GED.invokeLater");
		}
	}

}
