package com.myhome.chip;

public abstract class IOReadAndWrite {

	protected long ioUpdateTime;

	public long getIoUpdateTime() {
		return ioUpdateTime;
	}

	public synchronized IOChip getPios() {
		return pios.getCopy();
	}

	public abstract boolean postSetPIO(IOP pio);

	public abstract boolean postQueryPIO();

	protected IOChip pios = new IOChip();
}
