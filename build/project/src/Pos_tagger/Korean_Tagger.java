package Pos_tagger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Korean_Tagger {
	private static String[] CJJ = { "cho", "jun", "jon" };

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
	
	private static ArrayList<String> tp_rst;
	
	private static Korea korea = null;
	private static Dic_creater dic = null;
	private static List<String> Login = null;
	private static Map<String, String> Dic = null;
	
	public Korean_Tagger() throws IOException {
		
		korea = new Korea();
		dic = new Dic_creater();
		dic.setCreater();

		Login = dic.get_Login();
		Dic = dic.get_Dic();
	}
	
	public static ArrayList<String> getKorean(String inputtext) throws IOException {
		tp_rst = new ArrayList<String>();

		ArrayList<String> fin = new ArrayList<String>();
		//Scanner sc = new Scanner(System.in);

		//String input = sc.nextLine();
		String input = inputtext;
		
		// 분할하지 않을 경우 두 단어간의 접속 관계가 너무 많음
		// 동작 편의와 시간을 고려하여 공백을 기준으로 음절을 나누어서 태깅
		//String[] input_split = input.split(" ");

		for (int t = 0; t < 1; t++) {
		//	List<Map<String, Integer>> list = korea.get_Chosung(input_split[t]);
			List<Map<String, Integer>> list = korea.get_Chosung(input);

			//System.out.println(input_split[t]);

			// 감기는 ㄱㅏㅁㄱㅣㄴㅡㄴ
			// ㄴㅡㄴ ㅣㄱ ㅁㅏㄱ

			// 초중종성으로 분리한 문자열 저장
			char[] s = new char[list.size() * 3];
			int[] idx = new int[list.size() * 3];
			int[] cjj = new int[list.size() * 3];

			for (int i = 0; i < cjj.length; i++) {
				cjj[i] = (cjj.length - i - 1) % 3;
			}

			int s_idx = 0;
			for (int i = list.size() - 1; i >= 0; i--) {
				Map<String, Integer> tmp = list.get(i);

				int cho = tmp.get("cho");
				int jun = tmp.get("jun");
				int jon = tmp.get("jon");

				idx[s_idx] = jon;
				s[s_idx++] = JON[jon];
				idx[s_idx] = jun;
				s[s_idx++] = JUN[jun];
				idx[s_idx] = cho;
				s[s_idx++] = CHO[cho];
			}

			Map<Integer, ArrayList<String>> save = new HashMap<Integer, ArrayList<String>>();

			// 2종 1중 0초
			int parsing_idx = 2;
			int parsing_cnt = 0;
			for (int i = 0; i < s.length; i++) {
				if (parsing_idx == 2 && s[i] == 0x0000) {
					parsing_idx--;
					continue;
				}

				if (parsing_idx == 1) {
					parsing_idx--;
					continue;
				}

				Map<String, Integer> word = new HashMap<String, Integer>();
				ArrayList<String> l = new ArrayList<String>();
				ArrayList<String> tpl = new ArrayList<String>();
				
				String tmp = "";
				int local_s_idx = parsing_idx;
				for (int j = i; j >= 0; j--) {
					if (s[j] == 0) {
						continue;
					}

					tmp = korea.get_parsing_str(i, j, s, idx, cjj);

					//System.out.println(tmp);

					// 사전 검색
					if (Dic.containsKey(tmp)) {
						//System.out.println(tmp);
						int pre = j - 1;
						int first_jon = 0;

						if (s[0] == 0)
							first_jon = 1;

						if (pre < first_jon) {
							String[] tmp_log = Dic.get(tmp).split(" ");
							
							for (int w = 0; w < tmp_log.length; w++)
								l.add(tmp + "*" + tmp_log[w]);
							
						} else {
							if (s[pre] == 0x0000)
								pre -= 1;

							if (save.get(pre) != null) {

								ArrayList<String> pre_blue = save.get(pre);
								
								for (int c = 0; c < pre_blue.size(); c++) {									
									String pre_s = pre_blue.get(c);
									String pre_s1 = pre_s.split("\\*")[0];

									String pre_log = pre_s.split("\\+")[0].split("\\*")[1];

									String[] tmp_log = Dic.get(tmp).split(" ");

									String Login_info = "";
									for (int w = 0; w < tmp_log.length; w++) {
										Login_info = tmp_log[w] + "+" + pre_log;
										if (Login.contains(Login_info)) {
											l.add(tmp + "*" + tmp_log[w] + "+" + pre_s);
										}
									}
								}
							}
						}
					}
				}

				save.put(i, l);

				parsing_idx--;
				if (parsing_idx == -1)
					parsing_idx = 2;
			}

			// Tabular Parsing 결과 출력
			for(Integer key:save.keySet()) {
				ArrayList<String> fp = save.get(key);
				
				//System.out.println();
				
				String tps_rst = "";
				String pre = "";
				for(int i=0; i<fp.size(); i++) {
					String tps = fp.get(i);
					String[] tps1 = tps.split("\\+");
					
					for(int ti = 0; ti<tps1.length; ti++) {
						if(ti > 0)
							tps_rst += "+";
						
						tps_rst += tps1[ti].split("\\*")[0];
					}
					
					if(tps_rst.equals(pre)) {
						tps_rst = "";
						continue;
					}
					
					tp_rst.add(tps_rst);
					//System.out.println(tps_rst);
					pre = tps_rst;
					tps_rst = "";
				}
			}
			
//			System.out.println();
//			
//			for(Integer key:save.keySet()) {
//				ArrayList<String> fp = save.get(key);
//				System.out.println(key);
//				for(int i=0; i<fp.size(); i++) {
//					System.out.println(fp.get(i));
//				}
//			}
			
			fin.addAll(save.get(s.length - 1));
		}

//		for (int i = 0; i < fin.size(); i++) {
//			System.out.println(fin.get(i));
//		}
		
		return fin;
	}
	
	public ArrayList<String> get_tp_rst() {
		return tp_rst;
	}
}