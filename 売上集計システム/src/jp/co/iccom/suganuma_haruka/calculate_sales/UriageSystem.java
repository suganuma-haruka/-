package jp.co.iccom.suganuma_haruka.calculate_sales;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UriageSystem {
	public static void main(String[] args) {
		//mapに支店定義ファイルの中身を格納
		HashMap<String, String> shitenCode = new HashMap<String, String>();

		//mapに商品定義ファイルの中身を格納
		HashMap<String, String> syouhinCode = new HashMap<String, String>();

		//売上0円のHashMap
		HashMap<String,Integer> uriageMap = new HashMap<String,Integer>();

		//商品0円のHashMap
		HashMap<String,Integer> syouhinMap = new HashMap<String,Integer>();

		//売上ファイルの中身をArrayListに格納
		ArrayList<String> uriageList = new ArrayList<String>();


		//例外処理
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
					System.out.println(items[i]);
				}//for
				//支店コードが3桁でない場合のエラー処理
				String str = items[0];
				if(!str.matches("^\\d{3}$")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					break;
				}//if
				//shitenCodepにキーと値を追加
				shitenCode.put(items[0], items[1]);
				//uriageMapにキーと"0"を追加
				uriageMap.put(items[0],0);
			}//while
			//BufferReaderのオブジェクト終了
			br.close();
		//例外が発生したときの処理
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(e);
		}//try～catch

		//mapに格納されているキーと値を出力
		System.out.println(shitenCode.entrySet());
		System.out.println(uriageMap.entrySet());



		//例外処理
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
						System.out.println(items[i]);
				}//for
				//商品コードが半角英数字8桁でない場合のエラー処理
				String str = items[0];
				if(!str.matches("^\\w{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					break;
				}//if
				//syouhinCodeにキーと値を追加
				syouhinCode.put(items[0], items[1]);
				//syouhinMapにキーと"0"と値を追加
				syouhinMap.put(items[0], 0);
			}//while
			//BufferReaderのオブジェクト終了
			br.close();
		//例外が発生したときの処理
		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
			System.out.println(e);
		}//try～catch

		//map1に格納されているキーと値を出力
		System.out.println(syouhinCode.entrySet());
		System.out.println(syouhinMap.entrySet());


		//コマンドライン引数からディレクトリを指定
		File file = new File(args[0]);
		File files[] = file.listFiles();

		ArrayList<String> rcdList = new ArrayList<String>();

		//指定したディレクトリから"半角数字8桁.rcd"に該当するファイルの検索
		for(int i = 0; i < files.length; i++) {
			String str = files[i].getName();
			if(str.matches("^\\d{8}.rcd$")) {
				//該当ファイル名を"."で分割
				String[] items = (files[i].getName()).split("\\.");

				rcdList.add(items[0]);
			}//if
		}//for
		//要素数を元にファイル名が連番になっているかの確認
		String max = rcdList.get(rcdList.size() - 1);
		String min = rcdList.get(0);

		int x = Integer.parseInt(max);
		int y = Integer.parseInt(min);

		if(x - y == rcdList.size() - 1) {
			System.out.println(rcdList);
		} else {
			System.out.println("売上ファイル名が連番になっていません");
		}//if～else


		//売上ファイルの読み込み
		try {
			for(int i = 0; i < rcdList.size(); i++) {
				//連番を確認したファイルをコマンドライン引数を使って読み込み
				File file1 = new File(args[0], rcdList.get(i) + ".rcd");
				FileReader fr = new FileReader(file1);
				BufferedReader br = new BufferedReader(fr);
				String s;
				while((s = br.readLine()) != null) {
//					System.out.println(s);
					//addを使ってArrayListに要素として格納
					uriageList.add(s);
					System.out.println(uriageList);
				}//while

				br.close();
			}//for

		} catch(IOException e) {
			System.out.println(e);
		}//try～catch












	}
}

