package shared.heiliuer.shared;

public abstract class MyThread implements Runnable {
	protected boolean go;
	private int priority = Thread.MIN_PRIORITY;

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void start() {
		if (!go) {
			Thread thread = new Thread(this);
			thread.setPriority(priority);
			thread.start();
		}
	}

	public void stop() {
		go = false;
	}

	public boolean isGoing() {
		return go;
	}

	@Override
	abstract public void run();

}
