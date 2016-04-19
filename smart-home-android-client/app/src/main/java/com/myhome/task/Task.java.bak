package com.myhome.task;

import com.myhome.chip.IOReadAndWrite;

public abstract class Task<T extends Device> {

	public DayPeriod scheduleDayPeriod;

	public T device;

	public boolean canAction(DayPeriod dp) {
		return scheduleDayPeriod.hasIntersection(dp);
	}

	public abstract boolean action(IOReadAndWrite ioHandler);

	public abstract String getRemark();

}
