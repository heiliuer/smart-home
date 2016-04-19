package com.myhome.task;

public class TimeHms {

	public final int hours;
	public final int minutes;
	public final int seconds;

	public TimeHms(int days, int hours, int minutes, int seconds) {
		this.seconds = seconds;
		this.hours = hours;
		this.minutes = minutes;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && (o instanceof TimeHms)) {
			TimeHms tm = (TimeHms) o;
			return hours == tm.hours && minutes == tm.hours
					&& seconds == tm.seconds;
		}
		return false;
	}
}
