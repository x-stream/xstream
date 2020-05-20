package com.thoughtworks.xstream;

public class CloseListener {

	Thread thread;

	public CloseListener(Thread thread) {
		this.thread = thread;
	}

	public void close() {
		if (thread.isAlive())
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}