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

			//支店定義ファイルのHashMap
			HashMap<String, String> branchCode = new HashMap<String, String>();
			//商品定義ファイルのHashMap
			HashMap<String, String> commodityCode = new HashMap<String, String>();
			//支店別売上集計のHashMap
			HashMap<String, Long> branchTotal = new HashMap<String, Long>();
			//商品別売上集計のHashMap
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
					//支店定義ファイルのMapにキーと値を追加
					branchCode.put(branchitems[0], branchitems[1]);
					//支店別集計集計のMapにキーと"0"を追加
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
					if(!comCode.matches("^\\w{8}$") || commodityitems.length != 2) {
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					//商品定義ファイルのMapにキーと値を追加
					commodityCode.put(commodityitems[0], commodityitems[1]);
					//商品別売上集計のMapにキーと"0"と値を追加
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
					//支店別売上集計のMapに格納されている値かどうかの判定
					if(!branchTotal.containsKey(salesFile.get(0))) {
						System.out.println(fileName.get(i) + ".rcdの支店コードが不正です");
						return;
					}
					//売上ファイルの1行目の支店コードをキーにして値(金額)を集計
					long branchSum = branchTotal.get(salesFile.get(0));
					long braComSal = Integer.parseInt(salesFile.get(2));
					Long braTotalSum = branchSum + braComSal;

					//合計金額が10桁を超えたときのエラー
					if(braTotalSum.toString().length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

					//支店別売上集計のMapに格納
					branchTotal.put(salesFile.get(0), braTotalSum);

					//商品別売上集計のMapに格納されている値かどうかの判定
					if(!commodityTotal.containsKey(salesFile.get(1))) {
						System.out.println(fileName.get(i) + ".rcdの商品コードが不正です");
						return;
					}
					//売上ファイルの2行目の商品コードをキーにして値(金額)を集計
					long commoditySum = commodityTotal.get(salesFile.get(1));
					Long comTotalSum = commoditySum + braComSal;

					//合計金額が10桁を超えたときのエラー
					if(comTotalSum.toString().length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

					//商品別売上集計のMapに格納
					commodityTotal.put(salesFile.get(1), comTotalSum);
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

			//支店別売上集計ファイル
			BufferedWriter bwBranch = null;
			try {
				//ファイルに出力
				File branchFile = new File(args[0] + File.separator + "branch.out");
				FileWriter fw = new FileWriter(branchFile);
				bwBranch = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bwBranch);
				//Map.Entryのリストを作る
				List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(branchTotal.entrySet());
				//ComparatorでMap.Entryの値を比較
				Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
					//比較関数
					@Override
					public int compare(Map.Entry<String,Long> o1, Map.Entry<String,Long> o2) {
						return o2.getValue(). compareTo(o1.getValue()); //降順ソート
					}
				});
				//ソートした値を組み合わせて出力
				for(Map.Entry<String,Long> entrie : entries) {
					String branchKey = entrie.getKey();
					String branchName = branchCode.get(entrie.getKey());
					long branchSum = entrie.getValue();
					pw.println(branchKey + "," + branchName + "," + branchSum); //出力
				}
				//読み込み可能か確認、書き込み可能か確認
				if(!branchFile.canRead() || !branchFile.canWrite()) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if(bwBranch != null) {
					bwBranch.close();
				}
			}

			//商品別売上集計ファイル
			BufferedWriter bwCommodity = null;
			try {
				//ファイルに出力
				File commodityFile = new File(args[0] + File.separator + "commodity.out");
				FileWriter fw = new FileWriter(commodityFile);
				bwCommodity = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bwCommodity);
				//Map.Entryのリストを作る
				List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(commodityTotal.entrySet());
				//ComparatorでMap.Entryの値を比較
				Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
					//比較関数
					@Override
					public int compare(Map.Entry<String,Long> o1, Map.Entry<String,Long> o2) {
						return o2.getValue(). compareTo(o1.getValue()); //降順ソート
					}
				});
				//ソートした値を組み合わせて出力
				for(Map.Entry<String,Long> entrie : entries) {
					String commodityKey = entrie.getKey();
					String commodityName = commodityCode.get(entrie.getKey());
					long commoditySum = entrie.getValue();
					pw.println(commodityKey + "," + commodityName + "," + commoditySum); //出力
				}
				//読み込み可能か確認、書き込み可能か確認
				if(!commodityFile.canRead() || !commodityFile.canWrite()) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if(bwCommodity != null) {
					bwCommodity.close();
				}
			}
		} catch(Exception e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}