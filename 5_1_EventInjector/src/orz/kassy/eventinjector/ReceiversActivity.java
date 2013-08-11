
package orz.kassy.eventinjector;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 複数のBroadcastReceiverの入れ子となるActivity
 * Activityに仕事はない
 * ただしActivityを一度起動しないとBroadcastReceiverがうごないので注意
 */
public class ReceiversActivity extends Activity {

    private static final String TAG = "broadcastreceiver";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * ブロードキャストレシーバーのクラス これはAndroidManifest.xmlで登録する そのため、必ずstaticにする必要がある
     */
    static public class KeyEventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // インテントを受け取ったときの処理をここに記述
            Log.w(TAG, "hyaaReceive2 Broadcast and action = " + intent.getAction());
            Log.w(TAG, "scheme " + intent.getScheme());

            KeyEventSender sender = new KeyEventSender();
            sender.execute(KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_S,
                    KeyEvent.KEYCODE_S);
        }
    }

    static public class SlideEventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // インテントを受け取ったときの処理をここに記述
            Log.w(TAG, "Receive3 Broadcast and action = " + intent.getAction());
            int x = intent.getIntExtra("x", 0);
            int y = intent.getIntExtra("y", 0);
            int x2 = intent.getIntExtra("x2", 0);
            int y2 = intent.getIntExtra("y2", 0);

            SlideEventSender sender = new SlideEventSender();
            sender.execute(x, y, x2, y2);
        }
    }

    static public class SimpleTapBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // インテントを受け取ったときの処理をここに記述
            int x = intent.getIntExtra("x", 0);
            int y = intent.getIntExtra("y", 0);
            Log.w(TAG, "receive4 Broadcast and action = " + intent.getAction() + "(x,y)" + x+","+y);
            SimpleTapEventSender sender = new SimpleTapEventSender();
            sender.execute(x, y);
        }
    }

    private static void wait_msec(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 単純なタップを再現するタスク
     **/
    public static class SimpleTapEventSender
            extends AsyncTask<Integer, Integer, Integer> {
        /**
         * @param Integer : x座標、ｙ座標
         */
        @Override
        protected Integer doInBackground(Integer... params) {
            Log.i(TAG, "pushsync aaa");
            if (params.length < 2)
                return null;

            Instrumentation ist = new Instrumentation();
            MotionEvent event;

            event = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN,
                    params[0],
                    params[1],
                    0);
            ist.sendPointerSync(event);
            wait_msec(400);
            event = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    params[0],
                    params[1],
                    0);
            ist.sendPointerSync(event);
            return null;
        }
    }

    /**
     * ムーブイベントを再現するタスク
     **/
    public static class SlideEventSender
            extends AsyncTask<Integer, Integer, Integer> {
        /**
         * @param Integer : 開始x座標、開始ｙ座標、終了ｘ座標、終了ｙ座標
         */
        @Override
        protected Integer doInBackground(Integer... params) {
            if (params.length < 4)
                return null;
            int count = 20;
            // Down
            Instrumentation ist = new Instrumentation();
            MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN,
                    params[0],
                    params[1],
                    0);
            ist.sendPointerSync(event);
            wait_msec(100);

            // Move
            for (int i = 0; i < count; i++) {

                MotionEvent event21 = MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_MOVE,
                        ((count - i) * params[0] + i * params[2]) / count,
                        ((count - i) * params[1] + i * params[3]) / count,
                        0);
                ist.sendPointerSync(event21);
                wait_msec(100);
            }

            // Up
            MotionEvent event3 = MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    params[2],
                    params[3],
                    0);
            ist.sendPointerSync(event3);

            return null;
        }
    }

    /**
     * キーイベントを送付する非同期タスク
     */
    public static class KeyEventSender
            extends AsyncTask<Integer, Integer, Integer> {

        /**
         * @param Integer : キーコード
         */
        @Override
        protected Integer doInBackground(Integer... params) {
            Log.i(TAG, "keysync");
            int count = params.length;
            for (int i = 0; i < count; i++) {
                Instrumentation ist = new Instrumentation();
                ist.sendKeyDownUpSync(params[i]);
                wait_msec(300);
            }
            return null;
        }
    }
}
