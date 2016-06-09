package jp.co.iccom.suganuma_haruka.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UriageSystem {
	public static void main(String[] args) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			File blanch = new File("C:\\java\\blanch.lst");
			FileReader fr = new FileReader(blanch);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {

				String[] items = s.split(",");
				for(int i = 0; i < items.length; i++) {
					System.out.println(items[i]);
				}
				
					map.put(items[0], items[1]);
						
						String str = items[0];
							if(str.matches("^\\d{3}$")) {
//								System.out.println(items[0]);
							} else {
								System.out.println("支店定義ファイルのフォーマットが不正です。1");
								break;
							}
							
			
						String str1 = items[1];
							if(str1.matches("\r\n")) {
//								System.out.println(items[1]);
							} else {
								System.out.println("支店定義ファイルのフォーマットが不正です。2");
							}
			}
			
			br.close();
			
		} catch(IOException a) {
			System.out.println("支店定義ファイルが存在しません。");
			System.out.println(a);
			
//		} catch(ArrayIndexOutOfBoundsException b) {
//			System.out.println("支店定義ファイルのフォーマットが不正です。");
//			System.out.println(b);
		}

		System.out.println(map.entrySet());

//		try {
//			File commodity = new File("C:\\java\\commodity.lst");
//			FileReader fR = new FileReader(commodity);
//			BufferedReader bR = new BufferedReader(fR);
//			String t;
//			while((t = bR.readLine()) != null) {
//
//				String[] items = t.split(",");
//				for(int i = 0; i < items.length; i++) {
//					System.out.println(items[i]);
//				}

//			}
//			bR.close();
//		} catch(IOException c) {
//			System.out.println("商品定義ファイルが存在しません。");
//			System.out.println(c);
//		}

//		try {
//			HashMap<String, String> map = new HashMap<String, String>();
//				map.put("SFT00001", "Wandows");
//				map.put("SFT00002", "Offace");
//				map.put("SFT00003", "Lanux");
//				map.put("SFT00004", "Solaras");

//		} catch(ArrayIndexOutOfBoundsException d) {
//			System.out.println("商品定義ファイルのフォーマットが不正です。");
//			System.out.println(d);
//		}
	}
}

