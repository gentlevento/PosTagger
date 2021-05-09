package Pos_tagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pos_tagger {
	private String result_string = "";
	private String total_pos_list = "";
	private ArrayList<String> ss;

	public void tagging(String input, Korean_Tagger kt) throws IOException {
		// 베이즈 이론 적용해서 최종 품사 태깅 결과 출
//		예를 들어 1,273,000 단어가 있는 corpus에 “flies”가 1,000번 나타났고, 그 중에 명
//		사로써 400번 동사로써 600번 사용되었다면
//		P(flies)  1000/1,273,000 = 0.0008
//		P(flies&명사)  400/1,273,000 = 0.0003
//		P(flies&동사)  600/1,273,000 = 0.0005
//		P(동사|flies)  0.0005/0.0008 = 0.625

		Korea korea = new Korea();
		Dic_creater dic = new Dic_creater();
		dic.setCreater();

		List<String> Login = dic.get_Login();
		Map<String, String> Dic = dic.get_Dic();

		// Korean_Tagger kt = new Korean_Tagger();
		ss = kt.getKorean(input);

		float final_rst = 0;
		int final_idx = 0;
		for (int w = 0; w < ss.size(); w++) {

			String s = ss.get(w);

			if(s.equals("next")) {
				result_string += ss.get(final_idx)+"\n";
				final_rst = 0;
				continue;
			}
			
			String[] s_split = s.split("\\+");
			ArrayList<String> word = new ArrayList<String>();
			ArrayList<String> pos = new ArrayList<String>();

			for (int i = 0; i < s_split.length; i++) {
				word.add(s_split[i].split("\\*")[0]);
				pos.add(s_split[i].split("\\*")[1]);
			}

			float mid_rst = 0;
			for (int c = 0; c < word.size(); c++) {
				int contain_word_cnt = 0;
				int total_num = Dic.size();
				String single_word = word.get(c);

				String[] pos_list = Dic.get(single_word).split(" ");

				for (String key : Dic.keySet()) {
					if (key.contains(single_word)) {
						contain_word_cnt++;
					}
				}

				int[] contain_pos_cnt = new int[pos_list.length];
				for (int i = 0; i < pos_list.length; i++) {
					for (String key : Dic.keySet()) {
						if (key.contains(single_word)) {
							if (Dic.get(key).contains(pos_list[i])) {
								contain_pos_cnt[i]++;
							}
						}
					}
				}

//				for(int i=0; i<contain_pos_cnt.length; i++)
//					total_pos_list += pos_list[i] + " : " + contain_pos_cnt[i]+"\n";

				int max = 0;
				for (int i = 0; i < contain_pos_cnt.length; i++) {
					if (contain_pos_cnt[i] > contain_pos_cnt[max]) {
						max = i;
					}
				}

				float word_per_total = (float) contain_word_cnt / total_num;
				float word_pos = (float) contain_pos_cnt[max] / total_num;
				float rst = word_pos / word_per_total;

				// System.out.println("word list : "+total_num);
				// System.out.println("P(word) : "+word_per_total);
				// System.out.println("P(word&"+pos_list[max]+") : "+word_pos);
				// System.out.println("P(pos|word) : "+rst);

				mid_rst += rst;
			}

			mid_rst /= word.size();
			// System.out.println(mid_rst/word.size());

			if (mid_rst > final_rst) {
				final_rst = mid_rst;
				final_idx = w;
			}
		}
		
		//result_string += ss.get(final_idx);
		
	}

	public String get_pos_rst() {
		return result_string;
	}

	public ArrayList<String> total_pos_list() {
		ArrayList<String> aa = new ArrayList<String>();
		
		for(String a:ss) {
			if(!a.equals("next")) {
				aa.add(a);
			}
		}
		return aa;
	}
}