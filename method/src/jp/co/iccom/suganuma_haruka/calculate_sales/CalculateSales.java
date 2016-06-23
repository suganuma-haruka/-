package jp.co.iccom.suganuma_haruka.calculate_sales;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalculateSales {
	public static void main(String[] args) {
		BufferedReader br = null;

		//支店定義ファイルのHashMap
		HashMap<String, String> branchMap = new HashMap<String, String>();
		//商品定義ファイルのHashMap
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		//支店別売上集計のHashMap
		HashMap<String, Long> branchSales = new HashMap<String, Long>();
		//商品別売上集計のHashMap
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();


		try {
			if(args.length != 1) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

			//支店定義ファイルの呼び出し
			if(!readDefinitionFile(new File(args[0], "branch.lst"), branchMap, branchSales, "^\\d{3}$", "支店")) {
				return;
			}
			//商品定義ファイルの呼び出し
			if(!readDefinitionFile(new File(args[0], "commodity.lst"), commodityMap, commoditySales, "^\\w{8}$", "商品")) {
				return;
			}

			File files[] = new File(args[0]).listFiles();

			ArrayList<String> salesList = new ArrayList<String>();

			//"半角数字8桁.rcd"に該当するファイルを格納
			for(int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if(fileName.matches("^\\d{8}.rcd$") && files[i].isFile()) {
					String[] items = (files[i].getName()).split("\\.");
					salesList.add(items[0]);
				}
			}
			//昇順ソート
			Collections.sort(salesList);
			//要素数を元にファイル名が連番になっているかの確認
			String max = salesList.get(salesList.size() - 1);
			String min = salesList.get(0);
			int maxFileNumber = Integer.parseInt(max);
			int minFileNumber = Integer.parseInt(min);
			if(maxFileNumber - minFileNumber != salesList.size() - 1) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}

			//売上ファイルの読み込み
			for(int i = 0; i < salesList.size(); i++) {
				br = new BufferedReader(new FileReader(new File(args[0], salesList.get(i) + ".rcd")));
				String rcdName;

				ArrayList<String> salesFile = new ArrayList<String>();

				while((rcdName = br.readLine()) != null) {
					salesFile.add(rcdName);
				}
				//売上ファイル内の行数確認
				if(salesFile.size() != 3) {
					System.out.println(salesList.get(i) + ".rcdのフォーマットが不正です");
					return;
				}
				//支店コードで売上集計
				if(!salesSummary(branchSales, salesFile, 0, salesList.get(i), "支店")) {
					return;
				}
				//商品コードで売上集計
				if(!salesSummary(commoditySales, salesFile, 1, salesList.get(i),"商品")) {
					return;
				}
			}

			//支店別売上集計結果の呼び出し
			if(!fileOutput(branchSales, new File(args[0], "branch.out"), branchMap)) {
				return;
			}
			//商品別売上集計結果の呼び出し
			if(!fileOutput(commoditySales, new File(args[0], "commodity.out"), commodityMap)) {
				return;
			}
		} catch(Exception e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

	}

	//定義ファイル読み込みメソッド
	public static boolean readDefinitionFile(File filePath, HashMap<String, String> readDefinitionList,
		HashMap<String, Long> readSalesMap, String codePattern, String name) {
			BufferedReader br = null;
			File definitionFile = filePath;
			if(!definitionFile.exists()) {
				System.out.println(name + "定義ファイルが存在しません");
				return false;
			}
			try {
				br = new BufferedReader(new FileReader(definitionFile));
				String definitionContents;
				while((definitionContents = br.readLine()) != null) {
					String[] items = definitionContents.split(",");
					String code = items[0];
					if(!code.matches(codePattern) || items.length != 2) {
						System.out.println(name + "定義ファイルのフォーマットが不正です");
						return false;
					}
					readDefinitionList.put(items[0], items[1]);
					readSalesMap.put(items[0], 0L);
				}
			} catch(Exception e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			} finally {
				try {
					if(br != null) {
						br.close();
					}
				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
			}
		return true;
	}

	//売上集計メソッド
	public static boolean salesSummary(HashMap<String, Long> readSalesMap, ArrayList<String> readSalesFile,
		int elementNumber, String salesFileName, String name) {

			//Mapされている値かどうかの判定
			if(!readSalesMap.containsKey(readSalesFile.get(elementNumber))) {
				System.out.println(salesFileName + ".rcdの" + name + "コードが不正です");
				return false;
			}
			//売上ファイルの1行目の支店コードをキーにして値(金額)を集計
			//売上ファイルの2行目の商品コードをキーにして値(金額)を集計
			long initial = readSalesMap.get(readSalesFile.get(elementNumber));
			long revenus = Long.parseLong(readSalesFile.get(2));
			Long salesAmounts = initial + revenus;

			//合計金額が10桁を超えたときのエラー
			if(salesAmounts.toString().length() > 10) {
				System.out.println("合計金額が10桁を超えました");
				return false;
			}
			readSalesMap.put(readSalesFile.get(elementNumber), salesAmounts);
			return true;
	}

	// 売上集計出力メソッド
	public static boolean fileOutput(HashMap<String, Long> readSalesMap, File filePath,
			HashMap<String, String> readListMap) {

		BufferedWriter bw = null;
		try {
			//ファイルに出力
			File outputFile = filePath;
			bw = new BufferedWriter(new FileWriter(outputFile));
			PrintWriter pw = new PrintWriter(bw);
			//読み込み・書き込み可能か確認
			if(!outputFile.canRead() || !outputFile.canWrite()) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			//降順ソート
			List<Map.Entry<String, Long>> entries = new ArrayList<Map.Entry<String, Long>>(readSalesMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {
				@Override
				public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
					return o2.getValue(). compareTo(o1.getValue());
				}
			});
			for(Map.Entry<String, Long> entry : entries) {
				String keyCode = entry.getKey();
				String eachName = readListMap.get(entry.getKey());
				long salesAmounts = entry.getValue();
				pw.println(keyCode + "," + eachName + "," + salesAmounts);
			}
			pw.close();
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}
}