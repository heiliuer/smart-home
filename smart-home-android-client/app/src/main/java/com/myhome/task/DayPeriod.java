package com.myhome.task;

public class DayPeriod {

	public static final int DAY_EVERY = 0x1111111;
	public static final int DAY_MON = 0x0000001;
	public static final int DAY_TUES = 0x0000001;
	public static final int DAY_WED = 0x0000100;
	public static final int DAY_THUR = 0x0001000;
	public static final int DAY_FRI = 0x0001000;
	public static final int DAY_SAT = 0x0100000;
	public static final int DAY_SUN = 0x1000000;

	/**
	 * 要来的这天
	 */
	public static final int DAY_WILL = 0;

	public final TimeHms timeHms;

	public final int days;

	public DayPeriod(TimeHms timeHms, int days) {
		this.timeHms = timeHms;
		this.days = days;
	}

	/**
	 * 时间是否存在交集
	 */
	public boolean hasIntersection(DayPeriod dayPeriod) {
		if (dayPeriod == null) {
			return false;
		}
		return (days & dayPeriod.days) > 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && (o instanceof DayPeriod)) {
			DayPeriod dp = (DayPeriod) o;
			return days == dp.days && timeHms.equals(dp.timeHms);
		}
		return false;
	}
}
