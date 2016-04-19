package com.myhome.chip;

public class IOChip {

	private IOP P0 = new IOP(IOP.TAG_P0);
	private IOP P1 = new IOP(IOP.TAG_P1);
	private IOP P2 = new IOP(IOP.TAG_P2);
	private IOP P3 = new IOP(IOP.TAG_P3);

	public IOChip(int p0, int p1, int p2, int p3) {
		setPIOS(p0, p1, p2, p3);
	}

	public void setPIOS(int p0, int p1, int p2, int p3) {
		P0.setP(p0);
		P1.setP(p1);
		P2.setP(p2);
		P3.setP(p3);
	}

	public IOChip() {
	}

	public IOP getPIO(int pioTag) {
		switch (pioTag) {
		case IOP.TAG_P0:
			return P0.getCopy();
		case IOP.TAG_P1:
			return P1.getCopy();
		case IOP.TAG_P2:
			return P2.getCopy();
		case IOP.TAG_P3:
			return P3.getCopy();
		}
		return null;
	}

	public IOChip getCopy() {
		return new IOChip(P0.getP(), P1.getP(), P2.getP(), P3.getP());
	}
}
