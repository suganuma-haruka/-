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
		HashMap<String,Long> branchFin = new HashMap<String,Long>();

		//商品0円のHashMap
		HashMap<String,Long> commodityFin = new HashMap<String,Long>();

		BufferedReader brBranch = null;
		try {
			//支店定義ファイルの読み込み
			File branch = new File(args[0] + File.separator + "branch.lst");
			FileReader fr = new FileReader(branch);
			brBranch = new BufferedReader(fr);
			String s;
			//1行ずつ読み込んだ値を"s"に代入し、"null"でなければ｛｝の処理を実行
			while((s = brBranch.readLine()) != null) {
				//ファイルのデータを","で分割
				String[] items = s.split(",");
				//変数"i"を"0"で初期化し、"i"の値が"items"の要素数より小さい場合に{}を実行し、
				//大きくなったらfor文の実行を終了、"i"の値を"1"増やす
				for(int i = 0; i < items.length; i++) {
				}//for
				//支店コードが3桁でない場合のエラー処理
				String str = items[0];
				if(!str.matches("^\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				} else if(items.length > 2 || items.length < 2) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}//if
				//shitenCodepにキーと値を追加
				branchCode.put(items[0], items[1]);
				//uriageMapにキーと"0"を追加
				branchFin.put(items[0],(long) 0);
			}//while
			//BufferReaderのオブジェクト終了
		//例外が発生したときの処理
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				brBranch.close();
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}//try～catch
		}//try～catch～finally

		BufferedReader brCommodity = null;
		try {
			//商品定義ファイルの読み込み
			File commodity = new File(args[0] + File.separator + "commodity.lst");
			FileReader fr = new FileReader(commodity);
			brCommodity = new BufferedReader(fr);
			String s;
			while((s = brCommodity.readLine()) != null) {
				//ファイルのデータを","で分割
				String[] items = s.split(",");
				for(int i = 0; i < items.length; i++) {
				}//for
				//商品コードが半角英数字8桁でない場合のエラー処理
				String str = items[0];
				if(!str.matches("^\\w{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				} else if(items.length > 2 || items.length < 2) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}//if
				//syouhinCodeにキーと値を追加
				commodityCode.put(items[0], items[1]);
				//syouhinMapにキーと"0"と値を追加
				commodityFin.put(items[0], (long) 0);
			}//while
			//BufferReaderのオブジェクト終了
		//例外が発生したときの処理
		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				brCommodity.close();
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}//try～catch
		}//try～catch～finally

		//コマンドライン引数からディレクトリを指定
		File file = new File(args[0]);
		File files[] = file.listFiles();

		//".rcd"のファイル名をArrayListに格納
		ArrayList<String> fileName = new ArrayList<String>();

		//指定したディレクトリから"半角数字8桁.rcd"に該当するファイルの検索
		for(int i = 0; i < files.length; i++) {
			String str = files[i].getName();
			//"str"が8桁の半角数字、拡張子".rcd"かつ、文字列が12である時
			if(str.matches("^\\d{8}.rcd$") && str.length() == 12) {
				//該当ファイル名を"."で分割
				String[] items = (files[i].getName()).split("\\.");
				fileName.add(items[0]);
			//文字列の末尾が"rcd"で、文字列が12ではない時
			} else if(str.contains("rcd") && str.length() != 12) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}//if～else
		}//for

		//要素数を元にファイル名が連番になっているかの確認
		for (int i = 0 ; i < files.length ; i++){
			String max = fileName.get(fileName.size() - 1);
			String min = fileName.get(0);
			int x = Integer.parseInt(max);
			int y = Integer.parseInt(min);
			if(!(x - y == fileName.size() - 1)) { //ファイル名が連番ではない場合のエラー
				System.out.println("売上ファイル名が連番になっていません");
				return;
			} else if(files[i].isDirectory()) { //フォルダが含まれている場合のエラー
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}//if
		}//for

		//売上ファイルの読み込み
		try {
			for(int i = 0; i < fileName.size(); i++) {
				//連番を確認したファイルをコマンドライン引数を使って読み込み
				File file1 = new File(args[0], fileName.get(i) + ".rcd");
				FileReader fr = new FileReader(file1);
				BufferedReader br = new BufferedReader(fr);
				String s;

				//売上ファイルの中身をArrayListに格納
				ArrayList<String> salesFile = new ArrayList<String>();

				while((s = br.readLine()) != null) {
					//addを使ってArrayListに要素として格納
					salesFile.add(s);
				}//while
				br.close();
				//"salesFile"内の行数確認
				if(salesFile.size() > 3 || salesFile.size() < 3) {
					System.out.println(fileName.get(i) + ".rcdのフォーマットが不正です");
					return;
				}//if
				//"branchFin"にmapされている値かどうかの判断
				if(!branchFin.containsKey(salesFile.get(0))) {
					System.out.println(fileName.get(i) + ".rcdの支店コードが不正です");
					return;
				}//if
				//"salesFile"1行目の支店コードをキーにして金額を集計
				long branchSum = branchFin.get(salesFile.get(0));
				long branchSal = Integer.parseInt(salesFile.get(2));
				long braTotalSum = branchSum + branchSal;

				//"branchFin"のmapに格納
				branchFin.put(salesFile.get(0),braTotalSum);

				//合計金額が10桁を超えたときのエラー
				if(braTotalSum > 999999999) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}//if
				//"commodityFin"にmapされている値かどうかの判断
				if(!commodityFin.containsKey(salesFile.get(1))) {
					System.out.println(fileName.get(i) + ".rcdの商品コードが不正です");
					return;
				}//if
				//"salesFin"2行目の商品コードをキーにして金額を集計
				long commoditySum = commodityFin.get(salesFile.get(1));
				long commoditySal = Integer.parseInt(salesFile.get(2));
				long comTotalSum = commoditySum + commoditySal;

				//"commodityFin"のmapに格納
				commodityFin.put(salesFile.get(1),comTotalSum);

				//合計金額が10桁を超えたときのエラー
				if(comTotalSum > 999999999) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}//if
			}//for
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch(NumberFormatException e) {
			System.out.println("合計金額が10桁を超えました");
			return;
		}//try～catch

		//支店別集計ファイル
		BufferedWriter bwBranch = null;
		try {
			//ファイルに出力
			File branchFile = new File(args[0] + File.separator + "branch.out");
			branchFile.createNewFile();
			FileWriter fw = new FileWriter(branchFile);
			bwBranch = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bwBranch);
			//Map.Entryのリストを作る
			List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(branchFin.entrySet());
			//ComparatorでMap.Entryの値を比較
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
				//比較関数
				@Override
				public int compare(Map.Entry<String,Long> o1, Map.Entry<String,Long> o2) {
					return o2.getValue(). compareTo(o1.getValue()); //降順ソート
				}
			});
			//ソートした値を組み合わせて出力
			for(Map.Entry<String,Long> e : entries) {
				String branchKey = e.getKey();
				String branchName = branchCode.get(e.getKey());
				long branchSum = e.getValue();
				pw.println(branchKey + "," + branchName + "," + branchSum); //出力
			}//for
			//読み込み可能か確認
			if(!branchFile.canRead()) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			//書き込み可能か確認
			} else if(!branchFile.canWrite()) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}//if～else
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				bwBranch.close();
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}//try～catch
		}//try～catch～finally

		//商品別集計ファイル
		BufferedWriter bwCommodity = null;
		try {
			//ファイルに出力
			File commodityFile = new File(args[0] + File.separator + "commodity.out");
			commodityFile.createNewFile();
			FileWriter fw = new FileWriter(commodityFile);
			bwCommodity = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bwCommodity);
			//Map.Entryのリストを作る
			List<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(commodityFin.entrySet());
			//ComparatorでMap.Entryの値を比較
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
				//比較関数
				@Override
				public int compare(Map.Entry<String,Long> o1, Map.Entry<String,Long> o2) {
					return o2.getValue(). compareTo(o1.getValue()); //降順ソート
				}
			});
			//ソートした値を組み合わせて出力
			for(Map.Entry<String,Long> e : entries) {
				String commodityKey = e.getKey();
				String commodityName = commodityCode.get(e.getKey());
				long commoditySum = e.getValue();
				pw.println(commodityKey + "," + commodityName + "," + commoditySum); //出力
			}//for
			//読み込み可能か確認
			if(!commodityFile.canRead()) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			//書き込み可能か確認
			} else if(!commodityFile.canWrite()) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}//if～else
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				bwCommodity.close();
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}//try～catch
		}//try～catch～finally
	}//main
}//class