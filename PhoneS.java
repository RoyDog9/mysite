import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class PhoneS extends Frame implements ActionListener { // ActionListerを使ってボタンを反応させる

  // ■ フィールド変数
  TextField txt1; // 名前を入力するため
  Label lb1,lb2; // 文字を描画する為
  Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9; // 画面に配置するボタンの定義
  boolean m1=true,m2=false,m3=false,d=true; // モード変更用のフラグ
  boolean push=true,f1=false,f2=false,f3=false; // ボタンを片付けるためのフラグ

  byte[] b_ss; // writeを使うために文字列をバイト配列にする、その際格納するためのもの
  byte[] b_ss2 = new byte[24]; //相手からのバイト配列を格納するためのもの
  int a = 1; // 通話機能の変更に使う変数

  boolean i=true,j=true,k=true,l=true; // 通話機能を知らせる文を一度しか表示させないようにするフラグ
  boolean x=true; //whileの条件式がtrueだとコンパイルエラーになるため用意

  int BITS = 16; // 16bit
  int HZ = 8000; //周波数
  int MONO = 1; //１秒
  byte[] voice = new byte[ HZ * BITS / 8 * MONO ]; //声を送るためのバイト配列。一秒分のデータが入る。８で割っているのはビットをバイトにするため。
  byte[] invoice = new byte[ HZ * BITS / 8 * MONO ];//送られてきたバイト配列を入れるためのもの
  byte[] zero = new byte[HZ * BITS / 8 * MONO]; //ミュートに使うバイト配列、何も入っていない
  byte[] zero2 = new byte[HZ * BITS / 8 * MONO]; //マイクオフに使うバイト配列、何も入っていない

  String ss = "名無し",ss2; //名前の保存用、名前を設定しないと名無しで送られる

  int nPort = 1111; //ポート番号は予め決めておいた

  Record Rec = new Record() ; //Recordクラスを作成
  Play Play = new Play(); //Playクラスを作成
  Thread Th = new Thread(); //スレッドを作成,なくても動いた

  public static void main(String [] args) { // メイン文
    PhoneS ps = new PhoneS(); // PhoneS()を行う
  }

  // ■ コンストラクタ
  PhoneS() {
    super("まぐろ server"); //実行時につく名前
    this.setSize(500, 400); //画面のサイズ500×400
    setLayout(null); //絶対座標でボタンを配置したいのでオフに
    this.setLocationRelativeTo(null); //これで実行時に出るウィンドがPC画面の真ん中になる
    while(true){ //無限ループ

      try{ //sleepを使うためのtry-catch
      Thread.sleep(50); //これを入れるとボタンが反応するようになった
    }catch(Exception e) {System.out.println("Exception: " + e);}


        if(m1){ //モード１　タイトル画面

        lb1 = new Label(); //定義　
        lb1.setText("まぐろ"); //まぐろという文字を描写
        lb1.setFont(new Font("Arial", Font.BOLD+Font.ITALIC , 100));//フォントの指定
        lb1.setForeground(Color.black); //色は黒
        lb1.setBounds(100,120,300,100); //座標と大きさ
        add(lb1); //

        btn2 = new Button("名前変更");  // ボタンの設定
        btn2.addActionListener(this);     // ボタンのイベント処理
        add(btn2);                        // ボタンの登録
        btn2.setLocation(100, 350); //　座標の入力
        btn2.setSize(140, 30); //　ボタンの大きさ設定

        btn3 = new Button("終了");  // ボタンの設定
        btn3.addActionListener(this);     // ボタンのイベント処理
        add(btn3);                        // ボタンの登録
        btn3.setLocation(260, 350);
        btn3.setSize(140, 30);

        btn1 = new Button("通話開始");  // ボタンの設定
        btn1.addActionListener(this);     // ボタンのイベント処理
        add(btn1);                        // ボタンの登録
        btn1.setLocation(100, 320);
        btn1.setSize(300, 30);

        this.setVisible(true); //可視化

        m1 = false; //無限ループするたびに実行されると困るので、自らフラグをおろす
      }

    if(f1){ // モード１のラベル、ボタンを片付ける
      remove(btn1);
      remove(btn2);
      remove(btn3);
      remove(lb1);
      f1 = false; //自らフラグをおろす。存在しないボタンを消すとエラーになってしまう。
    }

      if(m2){ // モード２　　通話モード

        btn4 = new Button("通常通話");  // ボタンの設定
        btn4.addActionListener(this);     // ボタンのイベント処理
        add(btn4);                        // ボタンの登録
        btn4.setLocation(100, 130);
        btn4.setSize(300, 50);

        btn5 = new Button("ミュート");  // ボタンの設定
        btn5.addActionListener(this);     // ボタンのイベント処理
        add(btn5);                        // ボタンの登録
        btn5.setLocation(100, 183);
        btn5.setSize(300, 50);

        btn9 = new Button("マイクオフ");  // ボタンの設定
        btn9.addActionListener(this);     // ボタンのイベント処理
        add(btn9);                        // ボタンの登録
        btn9.setLocation(100, 236);
        btn9.setSize(300, 50);

        btn6 = new Button("終了");  // ボタンの設定
        btn6.addActionListener(this);     // ボタンのイベント処理
        add(btn6);                        // ボタンの登録
        btn6.setLocation(100, 330);
        btn6.setSize(300, 40);

        this.setVisible(true); // 可視化

        phone(); //後述の関数に入り、通話を行う。無限ループに入るのでここから出ることはない。
        m2 = false;

    }
    if(f2){ // モード２のボタンを片付けるためのものだが、これが使われることはない。消しても問題なし。
      remove(btn4);
      remove(btn5);
      remove(btn6);
      remove(btn9);
      f2 = false;
    }

      if(m3){ //モード３　名前変更モード

        txt1 = new TextField(""); add(txt1); // テキストフィールドを準備
        txt1.setLocation(100, 100); //座標を設定
        txt1.setSize(300, 30); //大きさを設定

        btn7 = new Button("この名前で決定");  // ボタンの設定
        btn7.addActionListener(this);     // ボタンのイベント処理
        add(btn7);                        // ボタンの登録
        btn7.setLocation(100, 280);
        btn7.setSize(300, 40);

        btn8 = new Button("戻る");  // ボタンの設定
        btn8.addActionListener(this);     // ボタンのイベント処理
        add(btn8);                        // ボタンの登録
        btn8.setLocation(100, 330);
        btn8.setSize(300, 40);

        this.setVisible(true);
        m3=false; //自らフラグをおろす

      }
      if(f3){ //モード３のテキストフィールド、ボタンを片付ける
        remove(txt1);
        remove(btn7);
        remove(btn8);
        f3 = false; //自らフラグをおろす
      }
    }
  }

  public void actionPerformed(ActionEvent e) { //ボタンが押された時の処理

    if (e.getSource() == btn1){ // ボタン１が押された時
      f1=true; //モード１から出るので、モード１のボタンを消すためのフラグを立てる
      m2=true; //モード２を行うためのフラグを立てる
    }
    else if(e.getSource() == btn2){
      f1=true; //モード１から出るので、モード１のボタンを消すためのフラグを立てる
      m3=true; //モード３を行うためのフラグを立てる
    }
    else if (e.getSource() == btn3)
      System.exit(0); //プログラムの終了
    else if(e.getSource() == btn4)
      a = 1; //通話モードの切り替え、ミュートに切り替える
    else if(e.getSource() == btn5)
      a = 0; //通話モードの切り替え、マイクオフに切り替える
    else if(e.getSource() == btn6){
      System.exit(0); //プログラムの終了

    }
    else if(e.getSource() == btn7){
      ss= txt1.getText();
    }
    else if(e.getSource() == btn8){
      f3=true; //モード3から出るので、モード3のボタンを消すためのフラグを立てる
      m1=true; //モード1を行うためのフラグを立てる
    }
    else if(e.getSource() == btn9){
      a=2; //通話モードの切り替え、通常通話を行う
    }

  }

  public void phone(){ //通話を行う関数
    try{

      ServerSocket serverSocket = new ServerSocket(nPort); //サーバーを立てる
      Socket skt = serverSocket.accept(); //クライアントが入ってくるのを待つ


      System.out.println("自分の名前は　" + ss + "　です。"); //ターミナルに自分の名前を出力する。画面には表示しない。


      OutputStream     os  = skt.getOutputStream(); //出力をソケットからもらう
      DataOutputStream dos = new DataOutputStream(os); //データの出力を可能にする

      InputStream     is  = skt.getInputStream(); //入力をソケットからもらう
      DataInputStream dis = new DataInputStream(is); //データの入力を可能にする


      b_ss = new byte[3*ss.length()]; //文字列の文字数の三倍の長さのバイト配列を用意。これだけあれば溢れることはない。
      b_ss = ss.getBytes(); //文字列をバイト配列に変換

      dos.write(b_ss); //writeの中に入れることで送ることができる
      dis.read(b_ss2); //readで相手が送ってきた名前を受け取る

      ss2= new String(b_ss2); //この文でバイト配列を文字列に復元できる

      lb2 = new Label(); //相手の名前を画面に表示する
      lb2.setText("  通話相手は　" + ss2 + "　さんです。"); //表示される文
      lb2.setFont(new Font("Arial", Font.BOLD+Font.ITALIC , 14));//フォントの指定
      lb2.setForeground(Color.black); //文字の色は黒
      lb2.setBounds(0,0,300,100); //座標と大きさ
      add(lb2);  //ラベルの登録

      System.out.println("通話相手は　" + ss2 + "　さんです。"); //ターミナルにも出力

      this.setVisible(true); //可視化

      Th.start(); //なくても動いた
      Rec.start(); //Record内のrunの開始、音声を読み込み続ける
      Play.start(); //Play内のrunの開始、スピーカーに音声を出力する

      while(x){ // 通話モードに入ったあとの無限ループ

        while(a==1){

          if(i){ //通常通話中ということを知らせる
          System.out.println( "通常通話中" );
          i=false;
          j=true;
        }

          voice = Rec.getVoice(); //getVoiceによってRecordクラス内にある音声データを返す
          dos.write(voice); //相手に音声を送る
          dis.read(invoice); //相手からの音声を受け取る
          Play.setVoice( invoice ); //Playクラス内の音声データを書き換える

        }

        while(a==0){ //相手の声遮断

          if(k){ //ミュート中ということを知らせる
          System.out.println( "ミュート中" );
          k=false;
          j=true;
        }
          voice = Rec.getVoice();
          dos.write(voice);
          dis.read(invoice);
          Play.setVoice( zero ); //用意しておいた何も入っていない配列をスピーカーに出力させる
        }

        while(a==2){ // 相手に声を届けない

          if(l){ //マイクオフ中ということを知らせる
          System.out.println( "マイクオフ中" );
          l=false;
          j=true;
        }
        voice = Rec.getVoice();
        dos.write(zero2); //相手に送る音声に何も入っていない配列を使うことで、自分の声を届けない
        dis.read(invoice);
        Play.setVoice( invoice );
      }

        if(j){ //全てのフラグを元に戻す（立てる）,マイクオフ後に通常通話に入ると文が表示されなくなることが防げる。
        i=true;
        k=true;
        l=true;
        j=false;
      }
    }
    Play.end(); //ソースデータラインを閉じることができる
    Rec.end(); //ターゲットデータラインを閉じることができる

    }catch(NumberFormatException  e){ //エラー処理
        System.err.println("引数はポーと番号です。1000〜65535までの数字を設定してください。"); //ポート番号は設定しているので削除しても問題はなさそう
    }catch(IndexOutOfBoundsException  e){
        System.err.println("引数はポート番号です。1000〜65535までの数字を設定してください。"); //ポート番号は設定しているので削除しても問題はなさそう
    }catch(IOException e){
        System.err.println("クライアントが見つかりません。どっか行きました\n" + e); //通話後にネットワークが切断されると、出力される
        System.exit(0);
    }catch(Exception e){
        System.err.println(e); //その他のエラー
    }

  }
}
