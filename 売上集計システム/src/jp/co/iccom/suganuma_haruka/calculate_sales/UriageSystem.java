package jp.co.iccom.suganuma_haruka.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UriageSystem {
	public static void main(String[] args) {
		try {
			File blanch = new File("C:\\java\\blanch.lst");
			FileReader fr = new FileReader(blanch);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {
//				System.out.println(s);

				String[] items = s.split(",");
				for(int i = 0; i < items.length; i++) {
					System.out.println(items[i]);
				}

//				String str5 = s;
//				String[] items2 = str.split(",");
//				for(int i = 0; i < items.length; i++) {
//					System.out.println(items2[i]);

//				}
			}
			br.close();
		} catch(IOException e) {
				System.out.println(e + "支店定義ファイルが存在しません。");
		}


		HashMap<String, String> map = new HashMap<String, String>();
			map.put("001", "札幌支店");
			map.put("002", "仙台支店");
			map.put("003", "東京支店");
			map.put("004", "名古屋支店");
			map.put("005","大阪支店");

//		try {
//			File commodity = new File("C:\\java\\commodity.lst");
//			FileReader fR = new FileReader(commodity);
//			BufferedReader bR = new BufferedReader(fR);
//			String p;
//			while((p = bR.readLine()) != null) {
//				System.out.println(p);
//			}
//			bR.close();
//			} catch(IOException d) {
//				System.out.println(d + "商品定義ファイルが存在しません。");
//			}
	}
}

