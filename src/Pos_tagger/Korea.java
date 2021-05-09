package Pos_tagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Korea {
	private static final char[] CHO =
			/* ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
			{ 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
					0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
	private static final char[] JUN =
			/* ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ */
			{ 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b,
					0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };

	/* X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ */
	private static final char[] JON = { 0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
			0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148,
			0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

	public List<Map<String, Integer>> get_Chosung(String tmpstr) {
		List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();

		for (int i = 0; i < tmpstr.length(); i++) {
			Map<String, Integer> m = new HashMap<String, Integer>();
			char chr = tmpstr.charAt(i);

			if (chr >= 0xAC00) {
				char uni = (char) (chr - 0xAC00);
				char cho = (char) (((uni - (uni % 28)) / 28) / 21);
				char jun = (char) (((uni - (uni % 28)) / 28) % 21);
				char jon = (char) (uni % 28);

				m.put("cho", (int) cho);
				m.put("jun", (int) jun);
				m.put("jon", (int) jon);

				list.add(m);
			}
		}

		return list;
	}

	public String get_String(List<Map<String, Integer>> list) {
		String rst = "";
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> n = list.get(i);
			int jon, cho, jun;

			char tmp;
			int tmp_n = 0;

			if (n.get("jon") != null) {
				tmp_n += n.get("jon");
			} else if (n.get("jun") != null) {
				tmp_n += 28 * (n.get("jun"));
			} else if (n.get("cho") != null) {
				tmp_n += 28 * 21 * n.get("cho");
			}

			tmp_n += 0xAC00;

			tmp = (char) tmp_n;

			// char tmp = (char) (0xAC00 + 28 * 21 * (cho) + 28 * (jun) + (jon));
			rst += tmp;
		}

		return rst;
	}

	public String get_Str(Map<String, Integer> n) {
		String rst = "";

		char tmp = 0;
		int tmpint = 0xAC00;

		boolean b_cho = n.containsKey("cho");
		boolean b_jun = n.containsKey("jun");
		boolean b_jon = n.containsKey("jon");

		int size = n.size();
		if (size == 3) {
			int cho = n.get("cho");
			int jun = n.get("jun");
			int jon = n.get("jon");

			tmp = (char) (0xAC00 + 28 * 21 * (cho) + 28 * (jun) + (jon));
		} else if (size == 2) {
			int cho = n.get("cho");
			int jun = n.get("jun");

			tmp = (char) (0xAC00 + 28 * 21 * (cho) + 28 * (jun) + (0));
		} else {

			if (b_cho) {
				int cho = n.get("cho");
				tmp += CHO[cho];
			} else if (b_jon) {
				int jon = n.get("jon");
				tmp += JON[jon];
			}
		}

		rst += tmp;

		return rst;
	}

	public String get_parsing_str(int start, int end, char[] s, int[] sidx, int[] idx) {
		// 2, 0
		// 2 1 0

		// 6, 5 4 3, 2
		String rst = "";
		char tmp = 0;

		if (idx[start] == 2) {

			rst += s[start];
			start = start - 1;

		}

		int i;
		for (i = start; i >= end;) {
			if (i - 2 >= end) {
				tmp = (char) (0xAC00 + 28 * 21 * (sidx[i]) + 28 * (sidx[i - 1]) + (sidx[i - 2]));
				rst += tmp;

				i -= 3;
			} else
				break;
		}

		// 2
		if (i == end) {
			rst += s[i];
		} else if (i == end + 1) {
			tmp = (char) (0xAC00 + 28 * 21 * (sidx[i]) + 28 * (sidx[i - 1]) + (0));
			rst += tmp;
		}

		return rst;
	}

}