package jp.co.iccom.suganuma_haruka.calculate_sales;

class BlanchList { // 支店定義クラス
	int id;               // 支店コード
	String blanchName;   // 支店名
	
	BlanchList(int id, String blanchName) {
		this.id = id;
		this.blanchName = blanchName;
	}
	
	// 情報を出力する
	void printInfo() {
		System.out.println(this.id);
		System.out.println(this.blanchName);
	}
}

class BlanchListManager {
	public static void main(String[] args) {
		BlanchList[] list = new BlanchList[100];
		list[0] = new BlanchList(001,"札幌支店");
		list[1] = new BlanchList(002,"仙台支店");
		list[2] = new BlanchList(003,"東京支店");
		list[3] = new BlanchList(004,"名古屋支店");
		list[4] = new BlanchList(005,"大阪支店");
		}
		
}

