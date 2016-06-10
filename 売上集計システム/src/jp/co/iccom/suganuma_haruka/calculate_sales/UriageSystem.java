package jp.co.iccom.suganuma_haruka.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UriageSystem {
	public static void main(String[] args) {
		System.out.println(args[0]);
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			File branch = new File(args[0], "branch.lst");
			FileReader fr = new FileReader(branch);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {

				String[] items = s.split(",");
				for(int i = 0; i < items.length; i++) {
					System.out.println(items[i]);
				}

				String str = items[0];
				if(!str.matches("^\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					break;

				}

				map.put(items[0], items[1]);
			}

			br.close();

		} catch(IOException a) {
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(a);
		}

		System.out.println(map.entrySet());










		HashMap<String, String> map1 = new HashMap<String, String>();
		try {
			File commodity = new File(args[0], "commodity.lst");
			FileReader fr = new FileReader(commodity);
			BufferedReader br = new BufferedReader(fr);
			String t;
			while((t = br.readLine()) != null) {

				String[] items = t.split(",");
				for(int i = 0; i < items.length; i++) {
						System.out.println(items[i]);
				}

				String str = items[0];
				if(!str.matches("^\\w{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					break;
				}

				map1.put(items[0], items[1]);
			}

			br.close();

		} catch(IOException b) {
			System.out.println("商品定義ファイルが存在しません");
			System.out.println(b);
		}

		System.out.println(map1.entrySet());



	}
}

