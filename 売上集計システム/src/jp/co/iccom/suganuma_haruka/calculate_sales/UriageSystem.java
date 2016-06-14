package jp.co.iccom.suganuma_haruka.calculate_sales;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UriageSystem {
	public static void main(String[] args) {
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

		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(e);
		}

		System.out.println(map.entrySet());



		HashMap<String, String> map1 = new HashMap<String, String>();
		try {
			File commodity = new File(args[0], "commodity.lst");
			FileReader fr = new FileReader(commodity);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {

				String[] items = s.split(",");
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

		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
			System.out.println(e);
		}

		System.out.println(map1.entrySet());



		File file = new File(args[0]);
		File files[] = file.listFiles();

		ArrayList<String> rcdList = new ArrayList<String>();

		for(int i = 0; i < files.length; i++) {
			String str = files[i].getName();
			if(str.matches("^\\d{8}.rcd$")) {

				String[] items = (files[i].getName()).split("\\.");

				rcdList.add(items[0]);
			}
		}

		String max = rcdList.get(rcdList.size() - 1);
		String min = rcdList.get(0);

		int x = Integer.parseInt(max);
		int y = Integer.parseInt(min);

		if(x - y == rcdList.size() - 1) {
			System.out.println(rcdList);
		} else {
			System.out.println("売上ファイル名が連番になっていません");
		}



		ArrayList<Integer> IntegerList = new ArrayList<Integer>();

		HashMap<String, String> map2 = new HashMap<String, String>();

		try {
			for(int i = 0; i < rcdList.size(); i++) {
				File file1 = new File(args[0], rcdList.get(i) + ".rcd");
				FileReader fr = new FileReader(file1);
				BufferedReader br = new BufferedReader(fr);
				String s;
//				while((s = br.readLine()) != null) {
//					System.out.println(s);

					ArrayList list = new ArrayList();
					list.add(br.readLine());
					list.add(br.readLine());
					list.add(br.readLine());
					System.out.println(list.get(0));
					System.out.println(list.get(1));
					System.out.println(list.get(2));

				}





//				String[] items = list;
//				for(int j = 0; j < items.length; j++) {
////					System.out.println(items[j]);
//				}
//
//				map2.put(items[0],items[3]);

//				br.close();
//			}

		} catch(IOException e) {
			System.out.println(e);
		}


//		System.out.println(map2.entrySet());



	}
}

