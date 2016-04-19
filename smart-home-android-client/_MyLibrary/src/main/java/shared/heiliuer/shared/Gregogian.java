package shared.heiliuer.shared;

import java.util.Calendar;

public class Gregogian {
	private int yYear;
	private int yMonth;
	private int yDay;
	private int week;
	private int weekTimes;

	public static Gregogian getInstance(long mill) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mill);
		return new Gregogian(c);
	}

	public static Gregogian getInstance(Calendar c) {
		return new Gregogian(c);
	}

	public static Gregogian getInstance() {
		return new Gregogian(Calendar.getInstance());
	}

	private Gregogian(Calendar c) {
		yYear = c.get(Calendar.YEAR);
		yMonth = c.get(Calendar.MONTH) + 1;
		yDay = c.get(Calendar.DATE);
		week = c.get(Calendar.DAY_OF_WEEK);
		weekTimes = c.get(Calendar.WEEK_OF_YEAR);
	}

	@Override
	public String toString() {
		return yYear + "年" + yMonth + "月" + yDay + "日  第" + getWeekTimes() + "周"
				+ " 周" + getChWeek();
	}

	public int getyYear() {
		return yYear;
	}

	public int getyMonth() {
		return yMonth;
	}

	public int getyDay() {
		return yDay;
	}

	/**
	 * 
	 * @return 1-7 1日 7六
	 */
	public int getWeek() {
		return week;
	}

	/**
	 * 
	 * @return 一二三四五六日
	 */
	public String getChWeek() {
		String w = null;
		switch (week) {
		case 1:
			w = "日";
			break;
		case 2:
			w = "一";
			break;
		case 3:
			w = "二";
			break;
		case 4:
			w = "三";
			break;
		case 5:
			w = "四";
			break;
		case 6:
			w = "五";
			break;
		case 7:
			w = "六";
		}
		return w;
	}

	/**
	 * 
	 * @return 得到周次
	 */
	public int getWeekTimes() {
		return weekTimes - baseWeek;
	}

	private int baseWeek;

	/**
	 * 
	 * @param baseWeek
	 *            起始周次
	 * @return
	 */
	public Gregogian setBaseWeek(int baseWeek) {
		if (baseWeek <= 0 || baseWeek > weekTimes)
			throw new IllegalArgumentException("baseweek 返回有问题");
		this.baseWeek = baseWeek;
		return this;
	}

	public static void main(String[] args) {
		System.out.print(getInstance(System.currentTimeMillis()));
	}

}
