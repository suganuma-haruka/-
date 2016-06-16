package jp.co.iccom.suganuma_haruka.calculate_sales;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class UriageSystem {
	public static void main(String[] args) {
		//mapに支店定義ファイルの中身を格納
		HashMap<String, String> branchCode = new HashMap<String, String>();

		//mapに商品定義ファイルの中身を格納
		HashMap<String, String> commodityCode = new HashMap<String, String>();

		//売上0円のHashMap
		HashMap<String,Integer> branchFin = new HashMap<String,Integer>();

		//商品0円のHashMap
		HashMap<String,Integer> commodityFin = new HashMap<String,Integer>();




		try {
			//支店定義ファイルの読み込み
			File branch = new File(args[0], "branch.lst");
			FileReader fr = new FileReader(branch);
			BufferedReader br = new BufferedReader(fr);
			String s;
			//1行ずつ読み込んだ値を"s"に代入し、"null"でなければ｛｝の処理を実行
			while((s = br.readLine()) != null) {
				//ファイルのデータを","で分割
				String[] items = s.split(",");
				//変数"i"を"0"で初期化し、"i"の値が"items"の要素数より小さい場合に{}を実行し、
				//大きくなったらfor文の実行を終了、"i"の値を"1"増やす
				for(int i = 0; i < items.length; i++) {
//					System.out.println(items[i]);
				}//for
				//支店コードが3桁でない場合のエラー処理
				String str = items[0];
				if(!str.matches("^\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					break;
				}//if
				//shitenCodepにキーと値を追加
				branchCode.put(items[0], items[1]);
				//uriageMapにキーと"0"を追加
				branchFin.put(items[0],0);
			}//while
			//BufferReaderのオブジェクト終了
			br.close();
		//例外が発生したときの処理
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(e);
		}//try～catch

		//mapに格納されているキーと値を出力
		System.out.println(branchCode.entrySet());



		try {
			//商品定義ファイルの読み込み
			File commodity = new File(args[0], "commodity.lst");
			FileReader fr = new FileReader(commodity);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {
				//ファイルのデータを","で分割
				String[] items = s.split(",");
				for(int i = 0; i < items.length; i++) {
//						System.out.println(items[i]);
				}//for
				//商品コードが半角英数字8桁でない場合のエラー処理
				String str = items[0];
				if(!str.matches("^\\w{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					break;
				}//if
				//syouhinCodeにキーと値を追加
				commodityCode.put(items[0], items[1]);
				//syouhinMapにキーと"0"と値を追加
				commodityFin.put(items[0], 0);
			}//while
			//BufferReaderのオブジェクト終了
			br.close();
		//例外が発生したときの処理
		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
			System.out.println(e);
		}//try～catch

		//map1に格納されているキーと値を出力
		System.out.println(commodityCode.entrySet());



		//コマンドライン引数からディレクトリを指定
		File file = new File(args[0]);
		File files[] = file.listFiles();

		//".rcd"のファイル名をArrayListに格納
		ArrayList<String> fileName = new ArrayList<String>();

		//指定したディレクトリから"半角数字8桁.rcd"に該当するファイルの検索
		for(int i = 0; i < files.length; i++) {
			String str = files[i].getName();
			if(str.matches("^\\d{8}.rcd$")) {
				//該当ファイル名を"."で分割
				String[] items = (files[i].getName()).split("\\.");

				fileName.add(items[0]);

			}//if
		}//for
		//要素数を元にファイル名が連番になっているかの確認
		String max = fileName.get(fileName.size() - 1);
		String min = fileName.get(0);

		int x = Integer.parseInt(max);
		int y = Integer.parseInt(min);

		if(x - y == fileName.size() - 1) {
//			System.out.println(fileName);
		} else {
			System.out.println("売上ファイル名が連番になっていません");
		}//if～else





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
//					System.out.println(s);
					//addを使ってArrayListに要素として格納
					salesFile.add(s);
//					System.out.println(uriageList);
				}//while

				br.close();


				//"salesFile"内の行数確認
				if(salesFile.size() > 3 || salesFile.size() > 3) {
					System.out.println("売上ファイル内のフォーマットが不正です");
					return;
				}


				//"branchFin"にmapされている値かどうかの判断
				if(!branchFin.containsKey(salesFile.get(0))) {
					System.out.println("売上ファイル内の支店コードが不正です");
					return;
				}

				//"salesFile"1行目の支店コードをキーにして金額を集計
				int branchSum = branchFin.get(salesFile.get(0));
				int branchSal = Integer.parseInt(salesFile.get(2));
				int braTotalSum = branchSum + branchSal;

				//"branchFin"のmapに格納
				branchFin.put(salesFile.get(0),braTotalSum);

				//合計金額が10桁を超えたときのエラー
				String braStr = Integer.toString(braTotalSum);
				if(braStr.matches("^\\d{10,}")) {
					System.out.println("合計金額が10桁を超えています");
					return;
				}



				//"commodityFin"にmapされている値かどうかの判断
				if(!commodityFin.containsKey(salesFile.get(1))) {
					System.out.println("売上ファイル内の商品コードが不正です");
					return;
				}
				//"salesFin"2行目の商品コードをキーにして金額を集計
				int commoditySum = commodityFin.get(salesFile.get(1));
				int commoditySal = Integer.parseInt(salesFile.get(2));
				int comTotalSum = commoditySum + commoditySal;

				//"commodityFin"のmapに格納
				commodityFin.put(salesFile.get(1),comTotalSum);

				//合計金額が10桁を超えたときのエラー
				String comStr = Integer.toString(comTotalSum);
				if(comStr.matches("^\\d{10,}")) {
					System.out.println("合計金額が10桁を超えています");
					return;
				}//if


			}//for

		} catch(IOException e) {
			System.out.println(e);
		}//try～catch

		//uriageMapに格納してあるキーと値を出力
		System.out.println(branchFin.entrySet());
		//syouhinMapに格納してあるキーと値を出力
		System.out.println(commodityFin.entrySet());

//		for(int i = 0; i < salesFile.size(); i++) {
//			System.out.println(salesFile.get(i));
//		}//for


		


		//支店別集計ファイル
		try {
			//ファイルに出力
			File branchFile = new File(args[0],"branch.out");
			FileWriter fw = new FileWriter(branchFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			//"branchCode"からキーを取得し、値を取り出す
			for(String branchKey : branchCode.keySet()) {
					pw.println(branchKey + "," + branchCode.get(branchKey) + "," + branchFin.get(branchKey));
			}
			bw.close();
			
		} catch(IOException e) {
			System.out.println(e);
		}


		//商品別集計ファイル
		try {
			//ファイルに出力
			File commodityFile = new File(args[0],"commodity.out");
			FileWriter fw = new FileWriter(commodityFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			//"commodityCode"からキーを取得し、値を取り出す
			for(String commodityKey : commodityCode.keySet()) {
					pw.println(commodityKey + "," + commodityCode.get(commodityKey) + "," + commodityFin.get(commodityKey));
			}
			bw.close();
			
		} catch(IOException e) {
			System.out.println(e);
		}



	}
}

