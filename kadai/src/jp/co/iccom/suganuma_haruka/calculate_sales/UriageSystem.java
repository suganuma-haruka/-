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

public class UriageSystem {
	public static void main(String[] args) {
		try{
			//コマンドライン引数の中身の確認
			if(args.length != 1) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

			//mapに支店定義ファイルの中身を格納
			HashMap<String, String> branchCode = new HashMap<String, String>();

			//mapに商品定義ファイルの中身を格納
			HashMap<String, String> commodityCode = new HashMap<String, String>();

			//売上0円のHashMap
			HashMap<String, Long> branchTotal = new HashMap<String, Long>();

			//商品0円のHashMap
			HashMap<String, Long> commodityTotal = new HashMap<String, Long>();

			BufferedReader brBranch = null;
			File branch = new File(args[0], "branch.lst");
			if(!branch.exists()) {
				System.out.println("支店定義ファイルが存在しません");
				return;
			}
			try {
				//支店定義ファイルの読み込み
				brBranch = new BufferedReader(new FileReader(branch));
				String branchLst;
				while((branchLst = brBranch.readLine()) != null) {
					String[] branchitems = branchLst.split(",");
					//支店コードが3桁でない場合のエラー処理
					String braCode = branchitems[0];
					if(!braCode.matches("^\\d{3}$") || branchitems.length > 2 || branchitems.length < 2) {
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					//shitenCodepにキーと値を追加
					branchCode.put(branchitems[0], branchitems[1]);
					//uriageMapにキーと"0"を追加
					branchTotal.put(branchitems[0], 0L);
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if(brBranch != null) {
					brBranch.close();
				}
			}

			BufferedReader brCommodity = null;
			File commodity = new File(args[0], "commodity.lst");
			if(!commodity.exists()) {
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			try {
				//商品定義ファイルの読み込み
				brCommodity = new BufferedReader(new FileReader(commodity));
				String commodityLst;
				while((commodityLst = brCommodity.readLine()) != null) {
					String[] commodityitems = commodityLst.split(",");
					//商品コードが半角英数字8桁でない場合のエラー処理
					String comCode = commodityitems[0];
					if(!comCode.matches("^\\w{8}$") || commodityitems.length > 2 || commodityitems.length < 2) {
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					//syouhinCodeにキーと値を追加
					commodityCode.put(commodityitems[0], commodityitems[1]);
					//syouhinMapにキーと"0"と値を追加
					commodityTotal.put(commodityitems[0], 0L);
				}
			//例外が発生したときの処理
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if(brCommodity != null) {
					brCommodity.close();
				}
			}

			//コマンドライン引数からディレクトリを指定
			File files[] = new File(args[0]).listFiles();

			//".rcd"のファイル名をArrayListに格納
			ArrayList<String> fileName = new ArrayList<String>();

			//指定したディレクトリから"半角数字8桁.rcd"に該当するファイルの検索
			for(int i = 0; i < files.length; i++) {
				String rcdName = files[i].getName();
				if(rcdName.matches("^\\d{8}.rcd$") && rcdName.length() == 12) {
					String[] items = (files[i].getName()).split("\\.");
					fileName.add(items[0]);
				} else if(rcdName.contains("rcd") && rcdName.length() != 12) {
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}

			//要素数を元にファイル名が連番になっているかの確認
			for (int i = 0 ; i < files.length ; i++){
				String max = fileName.get(fileName.size() - 1);
				String min = fileName.get(0);
				int maxName = Integer.parseInt(max);
				int minName = Integer.parseInt(min);
				if(maxName - minName != fileName.size() - 1) { //ファイル名が連番ではない場合のエラー
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
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
					//"salesFile"内の行数確認
					if(salesFile.size() > 3 || salesFile.size() < 3) {
						System.out.println(fileName.get(i) + ".rcdのフォーマットが不正です");
						return;
					}
					//"branchTotal"にmapされている値かどうかの判断
					if(!branchTotal.containsKey(salesFile.get(0))) {
						System.out.println(fileName.get(i) + ".rcdの支店コードが不正です");
						return;
					}
					//"salesFile"1行目の支店コードをキーにして金額を集計
					long branchSum = branchTotal.get(salesFile.get(0));
					long braComSal = Integer.parseInt(salesFile.get(2));
					Long braTotalSum = branchSum + braComSal;

					//"branchTotal"のmapに格納
					branchTotal.put(salesFile.get(0), braTotalSum);

					//合計金額が10桁を超えたときのエラー
					if(braTotalSum.toString().length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					//"commodityTotal"にmapされている値かどうかの判断
					if(!commodityTotal.containsKey(salesFile.get(1))) {
						System.out.println(fileName.get(i) + ".rcdの商品コードが不正です");
						return;
					}
					//"salesFile"2行目の商品コードをキーにして金額を集計
					long commoditySum = commodityTotal.get(salesFile.get(1));
					Long comTotalSum = commoditySum + braComSal;

					//"commodityTotal"のmapに格納
					commodityTotal.put(salesFile.get(1), comTotalSum);

					//合計金額が10桁を超えたときのエラー
					if(comTotalSum.toString().length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
				}
			} catch(IOException e) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			} catch(NumberFormatException e) {
				System.out.println("合計金額が10桁を超えました");
				return;
			} finally {
				if(brRcd != null) {
					brRcd.close();
				}
			}

			//支店別集計結果の呼び出し
			fileOutput(branchTotal, args[0], "branch.out", branchCode);
			//商品別集計結果の呼び出し
			fileOutput(commodityTotal, args[0], "commodity.out", commodityCode);

		} catch(Exception e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}

	// 売上集計出力メソッド
	public static void fileOutput(HashMap<String, Long> fileTotal, String directoryPath,
			String fileName, HashMap<String, String> fileOutCode) {

			BufferedWriter bw = null;
			try {
				//ファイルに出力
				File outputFile = new File(directoryPath, fileName);
				outputFile.createNewFile();
				bw = new BufferedWriter(new FileWriter(outputFile));
				PrintWriter pw = new PrintWriter(bw);
				//読み込み可能か確認、書き込み可能か確認
				if(!outputFile.canRead() || !outputFile.canWrite()) {
					System.out.println("予期せぬエラーが発生しました");
					return;
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
				for(Map.Entry<String, Long> e : entries) {
					String Key = e.getKey();
					String Name = fileOutCode.get(e.getKey());
					long Sum = e.getValue();
					pw.println(Key + "," + Name + "," + Sum); //出力
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				try {
					if(bw != null) {
						bw.close();
					}
				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
				}
			}
	}
}