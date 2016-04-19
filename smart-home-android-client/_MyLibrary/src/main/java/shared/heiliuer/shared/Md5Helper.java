package shared.heiliuer.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Helper {

	public static final int TYPE_MD5_32 = 0;
	public static final int TYPE_MD5_16 = 1;

	public static String getMd5(String plainText, int type)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(plainText.getBytes());
		byte b[] = md.digest();
		int i;
		StringBuffer buf = new StringBuffer();
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		if (type == TYPE_MD5_16)
			return buf.substring(8, 24);
		else
			return buf.toString();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.print(getMd5("玩很高", TYPE_MD5_32));

	}

}
