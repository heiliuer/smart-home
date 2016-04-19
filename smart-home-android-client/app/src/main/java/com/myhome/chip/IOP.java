package com.myhome.chip;

public class IOP {

	public static final int TAG_P0 = 0x00;
	public static final int TAG_P1 = 0x01;
	public static final int TAG_P2 = 0x02;
	public static final int TAG_P3 = 0x03;

	private int tag;

	public int getTag() {
		return tag;
	}

	private int P;

	public IOP(int tag, int P) {
		this.P = P & 0xff;
	}

	public IOP(int tag) {
		this.tag = tag;
	}

	public IOP setP(int P) {
		this.P = P & 0xff;
		return this;
	}

	public int getP() {
		return P;
	}

	public boolean isSbitLogicHigh(int pio) {
		return (P & (0x01 << pio)) != 0;
	}

	public IOP setSbitLogic(int pio, boolean isHigh) {
		if (isHigh) {
			P |= 0x01 << pio;
		} else {
			P &= 0xfe << pio;
		}
		return this;
	}

	public IOP getCopy() {
		return new IOP(tag, P);
	}

	public static final int PIO0 = 0;
	public static final int PIO1 = 1;
	public static final int PIO2 = 2;
	public static final int PIO3 = 3;
	public static final int PIO4 = 4;
	public static final int PIO5 = 5;
	public static final int PIO6 = 6;
	public static final int PIO7 = 7;

}
