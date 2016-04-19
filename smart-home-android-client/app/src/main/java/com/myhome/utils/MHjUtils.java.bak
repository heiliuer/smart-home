package com.myhome.utils;

import java.util.Arrays;

public class MHjUtils {

	public static final String byteArrayToHexString(byte datas[]) {
		StringBuilder builder = new StringBuilder(100);
		char[] tmp = new char[8];
		for (byte b : datas) {
			Arrays.fill(tmp, '0');
			char[] chars = Integer.toBinaryString(b & 0xff).toCharArray();
			int length = chars.length;
			int firstIndex = 8 - length;
			for (int i = 0; i < length; i++) {
				tmp[firstIndex + i] = chars[i];
			}
			for (char c : tmp) {
				builder.append(c);
			}
			builder.append('|');
		}
		String result = builder.toString();
		return result.substring(0, result.length() - 1);
	}

	public static final byte[] getByteFromChars(char datas[]) {
		byte results[] = new byte[8];
		System.arraycopy(datas, 0, results, 0, 8);
		return results;
	}

	public static final String charArrayToHexString(char datas[]) {
		StringBuilder builder = new StringBuilder(100);
		builder.append("{length: ").append(datas.length).append(";datas:");
		for (char b : datas) {
			builder.append(' ').append(Integer.toHexString(b & 0xff));
		}
		builder.append("}");
		return builder.toString();
	}

	public static byte[] int2Bytes(int num) {
		byte[] byteNum = new byte[4];
		for (int ix = 0; ix < 4; ++ix) {
			int offset = 32 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}

	public static int bytes2Int(byte[] byteNum) {
		int num = 0;
		for (int ix = 0; ix < 4; ++ix) {
			num <<= 8;
			num |= (byteNum[ix] & 0xff);
		}
		return num;
	}

	public static byte int2OneByte(int num) {
		return (byte) (num & 0x000000ff);
	}

	public static int oneByte2Int(byte byteNum) {
		return byteNum > 0 ? byteNum : (128 + (128 + byteNum));
	}

	public static void main(String[] args) {
		byte[] order = int2Bytes(1);
		int num = bytes2Int(order);
		System.out.println(num);
	}

}
