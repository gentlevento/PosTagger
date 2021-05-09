package Pos_tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dic_creater {
	private static List<String> Login = new ArrayList<String>();
	private static Map<String, String> Dic = new HashMap<String, String>();

	public void set_Login() throws IOException {
		// File dic = new
		// File(getClass().getClassLoader().getResource("log.txt").getFile());

		InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("log.txt"));
		// FileReader filereader = new FileReader(dic);

		BufferedReader bufReader = new BufferedReader(isr);

		String line = "";
		while ((line = bufReader.readLine()) != null) {
			Login.add(line);
		}

		bufReader.close();
	}

	public void set_Dic() throws IOException {
		// FileReader filereader = new FileReader(dic);

		InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("dic.txt"), "UTF-8");
		// FileReader filereader = new FileReader(dic);

		BufferedReader bufReader = new BufferedReader(isr);

		String line = "";
		while ((line = bufReader.readLine()) != null) {
			String[] strarr = line.split(": ");
			String word = strarr[1].split("\t")[0];
			String tag = strarr[2];

			// System.out.println(word + " " + tag);
			Dic.put(word, tag);	
		}
		
		System.out.println(Dic.get("주교"));
	}

	public List<String> get_Login() {
		return Login;
	}

	public Map<String, String> get_Dic() {
		return Dic;
	}

	public void setCreater() throws IOException {
		set_Login();
		set_Dic();
	}
}
