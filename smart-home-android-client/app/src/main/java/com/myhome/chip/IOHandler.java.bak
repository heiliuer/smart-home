package com.myhome.chip;

public abstract class IOHandler extends IOReadAndWrite {

	public synchronized void setPIOS(int p0, int p1, int p2, int p3) {
		pios.setPIOS(p0, p1, p2, p3);
		ioUpdateTime = System.currentTimeMillis();
	}

}
