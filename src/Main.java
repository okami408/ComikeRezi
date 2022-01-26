import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Main {
    public static void main(String[] args){
        //著作権表示
        System.out.println("Rezi System [Version 1.5.0]");
        System.out.println("(c) @KAKIMOTI1230 .All rights reserved");
        System.out.println("");
        //来客者数カウンター(1会計=1人)
        int raikyaku = 0;
        //商品チェック配列(商品が存在するかどうか識別、デフォルトはfalse)
        boolean[] hantei = new boolean[8];
        //アレイリスト宣言
        ArrayList<Item> items = new ArrayList<>();//商品管理リスト
        ArrayList<SellTime> st = new ArrayList<>();//売上時間リスト

        //ファイル読み込み
        String data;
        int count = 0;
        String[] temps = new String[4]; 
        try(BufferedReader br = new BufferedReader(new FileReader("input.txt"))){
            while((data = br.readLine()) != null){
                temps = data.split(",");
                //商品インスタンス生成
                Item item = new Item(Integer.parseInt(temps[0]),temps[1],Integer.parseInt(temps[2]),Integer.parseInt(temps[3]));
                items.add(item);//アレーリストに追加
                hantei[count] = true;
                count++;
            }

            //登録商品初回表示
            System.out.println("商品データ読み込み成功、データを登録しました");
            for(Item i : items){
                System.out.println(i.toString());
            }
            System.out.println("");

            //モード選択
            Scanner sc = new Scanner(System.in);
            System.out.println("*** モード選択 ***");
            System.out.println("0:レジ 1:売上記録 2:商品データ 3:売上記録訂正  9:システム終了");
            System.out.print("整数を入力してください ==>");
            int input = Integer.parseInt(sc.nextLine());
            while(input != 9){
                //レジシステム
                if(input == 0){
                    String input2 = "0";
                    int sumPrice = 0; //レジ時合計金額、初期化
                    System.out.println("");
                    System.out.println("*** レジシステム ***");
                    System.out.print("バーコードをスキャン ==>");
                    input2 = sc.nextLine();
                    while(input2 != ""){  //何も入力されない場合はレジシステムを終了する
                        int[] sellCount = new int[9];//売れた回数
                        ArrayList<Integer> kaikei = new ArrayList<>();//売上の一時保管、１会計毎にリセットする
                        int sCount = 0;

                        //商品販売ループ
                        while(input2 != ""){
                            //バーコード読み込み
                            int kazu = Main.codeRead(input2,hantei);
                            //商品が存在する場合
                            if(kazu >= 0){
                                System.out.println(items.get(kazu).getName() +":"+ items.get(kazu).getPrice() + "円");
                                System.out.println("");
                                kaikei.add(kazu);

                            //修正モード
                            } else if(kazu == -1){
                                System.out.println("*** 修正モード ***");
                                int counter = 0;
                                for(int ct : kaikei){
                                    System.out.print(counter + " : ");
                                    System.out.println(items.get(ct).getName());
                                    counter++;
                                }

                                System.out.println("削除したい番号を入力してください 全消し:334");
                                System.out.print("消したくない場合は何も入力せずエンター ==>");
                                String str = sc.nextLine();
                                //全消し
                                if(str.equals("334")){
                                    System.out.print("全消ししますか？ yes:0 NO:1 ==>");
                                    String temp = sc.nextLine();
                                    try{
                                        int sentaku = Integer.parseInt(temp);
                                        if(sentaku == 0){
                                            kaikei.clear();
                                            System.out.println("全消ししました");
                                            System.out.println("修正モードを終了します");
                                        } else {
                                            System.out.println("修正モードを終了します");
                                        }
                                    } catch(NumberFormatException e){
                                        System.out.println("指定された整数を入力してください");
                                    }
                                //何も消さない                                    
                                } else if(str.equals("")){
                                    ;
                                //指定した数を消す    
                                } else {
                                    try{
                                    int num = Integer.parseInt(str);
                                    kaikei.remove(num);
                                    } catch(IndexOutOfBoundsException e) {
                                        System.out.println("存在しない値を入力しないでください");
                                    } catch(NumberFormatException e){
                                        System.out.println("整数を入力してください");
                                    }
                                }
                            } else {
                                System.out.println("このコードは登録されていません");
                            }      
                            System.out.print("バーコードをスキャン ==>");
                            input2 = sc.nextLine();        
                        }
                        //売上処理ループ
                        System.out.println("*** お会計 ***");
                        for(int ct : kaikei){
                            //System.out.println(items.get(ct).getName() +":"+ items.get(ct).getPrice() + "円");
                            items.get(ct).setCount(1);//販売数+1
                            items.get(ct).setZaiko(-1);//在庫-1
                            sumPrice += items.get(ct).getPrice();//会計時合計金額に加算

                            //時間取得
                            LocalDateTime zikan = LocalDateTime.now();
                            //時間フォーマット指定
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

                            SellTime selltime = new SellTime(zikan.format(dtf),items.get(ct).getName()); //売上時間のインスタンス生成
                            st.add(selltime);//売上時間のリストに追加
                            System.out.println(items.get(ct).getName() +" : "+ items.get(ct).getPrice() + "円");
                        }
                        raikyaku++;//来客者数+1
                        System.out.println("合計金額:"+ sumPrice + "円");

                        sumPrice = 0; //レジ時合計金額、初期化
                        kaikei.clear();//売上アレーリスト初期化
      
                        System.out.println("");
                        System.out.println("終了する際はもう一度エンターを押してください");
                        System.out.print("バーコードをスキャン ==>");
                        input2 = sc.nextLine();        
                    }

                //売上記録確認(現バージョンでは一度プログラムを終了するとリセットされる)
                } else if(input == 1){
                    System.out.println("                ***** 売上記録 *****");
                    int allSumPrice = 0;
                    System.out.println("+----------------------------------+----------+----------+");
                    System.out.println("|  商  品  名                      | 販売個数 | 売上金額 |");
                    System.out.println("+----------------------------------+----------+----------+");
                    int j=0;

                    for(Item i : items){
                        String space = "";
                        for(int k=0; k < (13 - items.get(j).getName().length()); k++){
                            space = space + " ";
                        }

                        allSumPrice = allSumPrice + (i.getPrice() * i.getCount());
                       // System.out.println(i.getName() + ":" + i.getCount() +"ヶ販売,売上金額:"+ i.getPrice() * i.getCount() +"円");
                        System.out.printf("| %-20s%s|   %2d個   | %5d円  |\n", i.getName(), space, i.getCount(), i.getPrice() * i.getCount());
                        System.out.println("+----------------------------------+----------+----------+");
                        j++;
                    }
                    System.out.println("");

                    for(SellTime s : st){
                        System.out.println(s.toString());
                    }
                    System.out.println("");
                    System.out.println("売上合計:" + allSumPrice + "円");
                    System.out.println("来客数:" + raikyaku + "人");


                } else if(input == 2){//商品データ一覧
                    int j = 0;
                    System.out.println("                ***** 商品データ *****");
                    System.out.println("+----+----------------------------------+--------+-------+");
                    System.out.println("| ID |  商  品  名                      | 価 格  | 在 庫 |");
                 System.out.println("+----+----------------------------------+--------+-------+");
                    for(Item i : items){
                        String space = "";
                        for(int k=0; k < (13 - items.get(j).getName().length()); k++){
                            space = space + " ";
                        }
//System.out.println(items.get(j).getName().length());
                        System.out.printf("| %2d | %-20s%s| %4d円 | %3d個 |\n", i.getId(), i.getName(),space , i.getPrice(), i.getZaiko());
                        System.out.println("+----+----------------------------------+--------+-------+");
                        j++;
                    }
                
                //売上記録訂正
                } else if(input == 3){
                    System.out.println("*** 売上記録訂正 ***");
                    int i = 0;
                    for(SellTime s : st){
                        System.out.println(i +" : "+s.toString());
                        i++;
                    }
                    System.out.println("修正したいレコード番号を入力してください");
                    System.out.print("(修正しない場合はそのままエンター) ==>");
                    String str = sc.nextLine();
                    if(str.equals("")){
                        System.out.println("モード選択に戻ります");
                    } else {
                        try{
                            int num = Integer.parseInt(str);
                            st.remove(num);
                        } catch(IndexOutOfBoundsException e) {
                            System.out.println("エラー:存在しない値を入力しないでください");
                        } catch(NumberFormatException e){
                            System.out.println("エラー:整数を入力してください");
                        }
                    }

                } else {
                    System.out.println("その番号は登録されてません");
            }
                //再度モード選択
                System.out.println("");
                System.out.println("*** モード選択 ***");
                System.out.println("0:レジ 1:売上記録 2:商品データ 3:売上記録訂正  9:システム終了");
                System.out.print("整数を入力してください ==>");
                try {
                input = Integer.parseInt(sc.nextLine());
                } catch(NumberFormatException e){
                    System.out.println("指定された数値の整数を入力してください");
                    input = 999;
                }
            }
            //9が入力、プログラム終了
            System.out.println("お疲れさまでした、プログラムを終了します。");

        //エラー発生時の処理
        } catch(NumberFormatException e){//整数以外のデータ
            System.out.println("整数以外のデータが入力されています");
        } catch(FileNotFoundException e){//入力ファイルが見つからない
            System.out.println(e.getMessage());
        } catch(IOException e){//ファイルはあるが読み込めない
            System.out.println(e.getMessage());
        } finally {
            //売上記録をファイル出力する
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("Result.txt"))){
                int allSumPrice = 0; //全売上金額
                bw.write("*** 売上記録 ***");
                bw.newLine();
                for(Item i : items){
                    allSumPrice = allSumPrice + (i.getPrice() * i.getCount());
                    bw.write(i.getName() +":"+ i.getCount() +"ヶ販売,売上金額:"+ i.getPrice() * i.getCount() +"円");
                    bw.newLine();
                }
                bw.write("売上合計:" + allSumPrice + "円");
                bw.newLine();
                bw.write("来客数:" + raikyaku + "人");
                bw.newLine();

                try(BufferedWriter tl = new BufferedWriter(new FileWriter("TimeLog.txt"))){
                    for(SellTime s : st){
                        tl.write(s.toString());
                        tl.newLine();
                    }       
                }
            
            } catch(NumberFormatException e){
                System.out.println("エラー:整数を入力してください");
            } catch(IOException e){
                System.out.println("エラー:ファイル入出力エラー");
            } finally {
                System.out.println("レジシステムを終了します");
            }   
        }
    }
    //バーコード読みメソッド　返り値 正常:0以上 登録なし:-2 修正:-1
    public static int codeRead(String code, boolean[] hantei){
        if(code.equals("11111111111113")){
            if(hantei[0] == true){
                return 0;
            } else {
                return -2;
            }
        } else if(code.equals("22222222222226")) {
            if(hantei[1] == true){
                return 1;
            } else {
                return -2;
            }
        } else if(code.equals("33333333333339")){
            if(hantei[2] == true){
                return 2;
            } else {
                return -2;
            }    
        } else if(code.equals("44444444444442")){
            if(hantei[3] == true){
                return 3;
            } else {
                return -2;
            }
        } else if(code.equals("55555555555555")){
            if(hantei[4] == true){           
                return 4;
            } else {
                return -2;
            }
        } else if(code.equals("66666666666668")){
            if(hantei[5] == true){
                return 5;
            } else {
                return -2;
            }
        } else if(code.equals("77777777777771")){
            if(hantei[6] == true){        
              return 6;
            } else {
                return -2;
            }

        } else if(code.equals("88888888888884")){
            if(hantei[7] == true){
                return 7;
            } else {
                return -2;
            }
        //訂正コード
        } else if(code.equals("99999999999997")){
            return -1;

        //コード該当なし
        } else {
            return -2;
        }
    }
}