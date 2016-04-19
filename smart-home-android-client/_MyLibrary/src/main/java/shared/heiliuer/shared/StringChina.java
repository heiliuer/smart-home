package shared.heiliuer.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * 按汉字首字母比较大小
 * 
 * @author Administrator
 * 
 */
public class StringChina {

	public static ArrayList<String> sort(List<String> strs) {
		ArrayList<String> result = new ArrayList<String>();
		int i, j;
		String a, b;
		for (i = 0; i < strs.size(); i++) {
			a = strs.get(i);
			for (j = i + 1; j < strs.size(); j++) {
				b = strs.get(j);
				if (compare(a, b) > 0) {
					strs.remove(b);
					j--;
					a = b;
					Utils.l(a);
				}
			}
			result.add(a);
		}
		return result;
	}

	private static final char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static final char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发',
			'噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒',
			'塌', '塌', '塌', '挖', '昔', '压', '匝', '座' };

	private static int[] table = new int[27];

	public static char char2Alpha(char ch) {
		if (ch >= 'a' && ch <= 'z')
			return (char) (ch - 'a' + 'A');
		if (ch >= 'A' && ch <= 'Z')
			return ch;
		int gb = gbValue(ch);
		if (gb < table[0])
			return '0';
		int i;
		for (i = 0; i < 26; ++i) {
			if (match(i, gb))
				break;
		}
		if (i >= 26)
			return '0';
		else
			return alphatable[i];
	}

	/**
	 * 
	 * @param strA
	 * @param strB
	 * @return 1 if a>b,0 if a=b,-1 if a<b
	 */
	public static int compare(String strA, String strB) {
		if (strA == null || strA.length() == 0)
			return 0;
		else if (strB == null || strB.length() == 0) {
			return 1;
		} else {
			char charsA[] = strA.toCharArray();
			char charsB[] = strB.toCharArray();
			int length = Math.min(charsA.length, charsB.length);
			char a, b;
			for (int i = 0; i < length; i++) {
				if (charsA[i] != charsB[i]) {
					if (isChinese(charsA[i]) && isChinese(charsB[i])) {
						a = char2Alpha(charsA[i]);
						b = char2Alpha(charsB[i]);
					} else {
						a = charsA[i];
						b = charsB[i];
					}
					if (a > b)
						return 1;
					else if (a < b)
						return -1;
				}
			}
			return 0;
		}
	}

	// 取出汉字的编码
	private static int gbValue(char ch) {
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("gb2312");
			if (bytes.length < 2)
				return 0;
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return 0;
		}
	}

	public static boolean isChinese(char c) {
		return c >= 0x4e00 && c <= 0x9fa5;
	}

	private static boolean match(int i, int gb) {
		if (gb < table[i])
			return false;
		int j = i + 1;
		// 字母Z使用了两个标签
		while (j < 26 && (table[j] == table[i]))
			++j;
		if (j == 26)
			return gb <= table[j];
		else
			return gb < table[j];
	}

	public static String string2Alpha(String SourceStr) {

		String Result = "";
		int StrLength = SourceStr.length();
		int i;
		try {
			for (i = 0; i < StrLength; i++) {
				Result += char2Alpha(SourceStr.charAt(i));
			}
		} catch (Exception e) {
			Result = "";
		}
		return Result;
	}

	{
		for (int i = 0; i < 27; ++i) {
			table[i] = gbValue(chartable[i]);
		}
	}
}
