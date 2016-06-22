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
		try {
			//コマンドライン引数の中身の確認
			if(args.length != 1) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

			//支店定義ファイルのHashMap
			HashMap<String, String> branchCode = new HashMap<String, String>();
			//商品定義ファイルのHashMap
			HashMap<String, String> commodityCode = new HashMap<String, String>();
			//支店別売上集計のHashMap
			HashMap<String, Long> branchTotal = new HashMap<String, Long>();
			//商品別売上集計のHashMap
			HashMap<String, Long> commodityTotal = new HashMap<String, Long>();

			//支店定義ファイルの呼び出し
			if(!readDefinitionFile(new File(args[0], "branch.lst"), branchCode, branchTotal, "^\\d{3}$", "支店")) {
				return;
			}
			//商品定義ファイルの呼び出し
			if(!readDefinitionFile(new File(args[0], "commodity.lst"), commodityCode, commodityTotal, "^\\w{8}$", "商品")) {
				return;
			}

			//コマンドライン引数からディレクトリを指定
			File files[] = new File(args[0]).listFiles();

			ArrayList<String> fileName = new ArrayList<String>();

			//指定したディレクトリから"半角数字8桁.rcd"に該当するファイルの検索
			for(int i = 0; i < files.length; i++) {
				String rcdName = files[i].getName();
				if(rcdName.matches("^\\d{8}.rcd$")) {
					String[] items = (files[i].getName()).split("\\.");
					fileName.add(items[0]);
				}
			}
			//昇順ソート
			Collections.sort(fileName);
			//要素数を元にファイル名が連番になっているかの確認
			String max = fileName.get(fileName.size() - 1);
			String min = fileName.get(0);
			int maxName = Integer.parseInt(max);
			int minName = Integer.parseInt(min);
			if(maxName - minName != fileName.size() - 1) { //ファイル名が連番ではない場合のエラー
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}

			//売上ファイルの読み込み
			BufferedReader brRcd = null;
			try {
				for(int i = 0; i < fileName.size(); i++) {
					brRcd = new BufferedReader(new FileReader(new File(args[0], fileName.get(i) + ".rcd")));
					String rcdName;

					//売上ファイルの中身をArrayListに格納
					ArrayList<String> salesFile = new ArrayList<String>();

					while((rcdName = brRcd.readLine()) != null) {
						salesFile.add(rcdName);
					}
					//売上ファイル内の行数確認
					if(salesFile.size() != 3) {
						System.out.println(fileName.get(i) + ".rcdのフォーマットが不正です");
						return;
					}
					//支店コードで売上集計
					if(!collection(branchTotal, salesFile, 0, fileName.get(i), "支店")) {
						return;
					}
					//商品コードで売上集計
					if(!collection(commodityTotal, salesFile, 1, fileName.get(i),"商品")) {
						return;
					}
				}
			} finally {
				if(brRcd != null) {
					brRcd.close();
				}
			}

			//支店別売上集計結果の呼び出し
			if(!fileOutput(branchTotal, new File(args[0], "branch.out"), branchCode)) {
				return;
			}
			//商品別売上集計結果の呼び出し
			if(!fileOutput(commodityTotal, new File(args[0], "commodity.out"), commodityCode)) {
				return;
			}
		} catch(Exception e) {
			System.out.println("予期せぬエラーが発生しました");
		}
	}

	/**
	 * 定義ファイル読み込みメソッド
	 * @param filePath
	 * @param readCode
	 * @param fileTotal
	 * @param codePattern
	 * @param Name
	 * @return
	 */
	public static boolean readDefinitionFile(File filePath, HashMap<String, String> readCode,
		HashMap<String, Long> fileTotal, String codePattern, String Name) {
		try {
			BufferedReader br = null;
			File definitionFile = filePath;
			if(!definitionFile.exists()) {
				System.out.println(Name + "定義ファイルが存在しません");
				return false;
			}
			try {
				br = new BufferedReader(new FileReader(definitionFile));
				String fileLst;
				while((fileLst = br.readLine()) != null) {
					String[] lstitems = fileLst.split(",");
					String code = lstitems[0];
					if(!code.matches(codePattern) || lstitems.length != 2) {
						System.out.println(Name + "定義ファイルのフォーマットが不正です");
						return false;
					}
					readCode.put(lstitems[0], lstitems[1]);
					fileTotal.put(lstitems[0], 0L);
				}
			} finally {
					if(br != null) {
						br.close();
					}
			}
		} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
		}
		return true;
	}

	//売上集計メソッド
	public static boolean collection(HashMap<String, Long> fileTotal, ArrayList<String> collectionFile,
		int getNumber, String fileName, String Name) {

			//Mapされている値かどうかの判定
			if(!fileTotal.containsKey(collectionFile.get(getNumber))) {
				System.out.println(fileName + ".rcdの" + Name + "コードが不正です");
				return false;
			}
			//売上ファイルの1行目の支店コードをキーにして値(金額)を集計
			//売上ファイルの2行目の商品コードをキーにして値(金額)を集計
			long initial = fileTotal.get(collectionFile.get(getNumber));
			long sum = Long.parseLong(collectionFile.get(2));
			Long totalSum = initial + sum;

			//合計金額が10桁を超えたときのエラー
			if(totalSum.toString().length() > 10) {
				System.out.println("合計金額が10桁を超えました");
				return false;
			}
			fileTotal.put(collectionFile.get(getNumber), totalSum);
			return true;
	}

	// 売上集計出力メソッド
	public static boolean fileOutput(HashMap<String, Long> fileTotal, File directoryPath,
			HashMap<String, String> fileOutCode) {

		BufferedWriter bw = null;
		try {
			//ファイルに出力
			File outputFile = directoryPath;
			bw = new BufferedWriter(new FileWriter(outputFile));
			PrintWriter pw = new PrintWriter(bw);
			//読み込み可能か確認、書き込み可能か確認
			if(!outputFile.canRead() || !outputFile.canWrite()) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			//Map.Entryのリストを作る
			List<Map.Entry<String, Long>> entries = new ArrayList<Map.Entry<String, Long>>(fileTotal.entrySet());
			//ComparatorでMap.Entryの値を比較
			Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {
				//比較関数
				@Override
				public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
					return o2.getValue(). compareTo(o1.getValue()); //降順ソート
				}
			});
			//ソートした値を組み合わせて出力
			for(Map.Entry<String, Long> entrie : entries) {
				String key = entrie.getKey();
				String name = fileOutCode.get(entrie.getKey());
				long sum = entrie.getValue();
				pw.println(key + "," + name + "," + sum); //ファイルに出力
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