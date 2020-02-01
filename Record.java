
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Record extends Thread{

    private static final int BITS = 16; //16bit
    private static final int HZ = 8000; //8000Hz
    private static final int MONO = 1; //1秒

    // リニアPCM 16bit 8000Hz x １秒
    private byte[] voice = new byte[ HZ * BITS / 8 * MONO ]; //一秒分のバイト配列
    private TargetDataLine target; //マイクを使う
    private AudioInputStream stream; //　マイクからのデータを取得する

    public boolean g_bRecorder = false; //これがtrueでないと音声取得しない

    // コンストラクタ
    Record()
    {
        try
        {
            // オーディオフォーマットの指定
            AudioFormat linear = new AudioFormat( HZ, BITS, MONO, true, false );

            // ターゲットデータラインを取得
            DataLine.Info info = new DataLine.Info( TargetDataLine.class, linear );
            target = (TargetDataLine)AudioSystem.getLine( info );

            // ターゲットデータラインを開く
            target.open( linear );

            // マイク入力開始
            target.start();

            // 入力ストリームを取得
            stream = new AudioInputStream( target );

        } catch (LineUnavailableException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        g_bRecorder = true; //これがtrueでないと音声取得はしない
    }

    // スレッド実行
    public void run()
    {
        while( true )
        {
            if( !g_bRecorder ) return;
            try
            {
                stream.read( voice , 0, voice.length ); //マイクからの音声のデータ化

            } catch (IOException e)
            {

                e.printStackTrace();
            }

            try{// 一応、ウエイト
                Thread.sleep( 100 );
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    // データ取得
    public byte[] getVoice()
    {
        return voice ; //そのままvoice(音声データ)を返す
    }

    // 終了
    public void end()
    {
        g_bRecorder = false;

        // ターゲットデータラインを停止
        target.stop();

        // ターゲットデータラインを閉じる
        target.close();
    }
}
