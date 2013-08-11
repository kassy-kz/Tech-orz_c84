
package orz.kassy.nfcwizardglove;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SetTouchEventActivity extends SherlockActivity {

    private static final String TAG = null;

    private static final int OPTIONS_ITEM_ID_UP = 0;
    private static final int OPTIONS_ITEM_ID_DOWN = 1;

    private static Context sContext;

    private File[] mFileList;

    private int mCurFileNum;

    private ImageView mBackImageView;

    private int downY;

    private int downX;

    private int mFinger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        sContext = this;

        setContentView(R.layout.activity_settouchevent);

        TouchDetectView mHandleView = new TouchDetectView(this);
        FrameLayout base = (FrameLayout) findViewById(R.id.setTouchEventMain);
        base.addView(mHandleView);

        // どの指かを取得
        mFinger = getIntent().getIntExtra(AppUtils.INTENT_EXTRA_FINGER, 1);
        Log.i(TAG, "finger = " + mFinger);
        
        // スクリーンショットを表示
        File file = new File("/storage/emulated/legacy/Pictures/Screenshots");
        mFileList = file.listFiles();
        mCurFileNum = mFileList.length - 1;
        Drawable drw = Drawable.createFromPath(mFileList[mCurFileNum].getPath());
        mBackImageView = (ImageView) findViewById(R.id.touchBackImage);
        mBackImageView.setImageDrawable(drw);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(this, "△▽でスクリーンショットを切り替えます", Toast.LENGTH_LONG).show();
    }

    /**
     * OptionsMenuを生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, OPTIONS_ITEM_ID_UP, Menu.NONE, "Up")
                .setIcon(android.R.drawable.arrow_up_float)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, OPTIONS_ITEM_ID_DOWN, Menu.NONE, "Down")
                .setIcon(android.R.drawable.arrow_down_float)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    public class TouchDetectView extends View {

        int mTouchX = -100;
        int mTouchY = -100;
        int mReleaseX = -100;
        int mReleaseY = -100;

        public TouchDetectView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.i("View", "onTouchEvent " + event.getAction() + "(x,y)" + event.getX() + ","
                    + event.getY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchX = (int) event.getX();
                    mTouchY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    mReleaseX = (int) event.getX();
                    mReleaseY = (int) event.getY();
                default:
                    break;
            }
            invalidate();
            return false;
        }

        // 描画の度に呼ばれるメソッド
        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawLine(mTouchX - 10, mTouchY - 10, mTouchX + 10, mTouchY + 10, paint);
            canvas.drawLine(mTouchX + 10, mTouchY - 10, mTouchX - 10, mTouchY + 10, paint);

            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            canvas.drawLine(mReleaseX - 10, mReleaseY - 10, mReleaseX + 10, mReleaseY + 10, paint);
            canvas.drawLine(mReleaseX + 10, mReleaseY - 10, mReleaseX - 10, mReleaseY + 10, paint);
        }
    }

    /**
     * Activityにタッチイベントが発生したら呼ばれるメソッド
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 絶対座標でとれる
        Log.i("activity", "onTouchEvent " + event.getAction() + "(x,y)" + (int)event.getX() + "," + (int)event.getY());
        if(event.getPointerCount()>2) {
            Log.i("activity", "             " + (int)event.getX(1) + ", "+(int)event.getY(1) );
        }

        // タッチの種類で振り分け
        switch (event.getAction()) {
        // 押された
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                Log.i("act","downXY = "+event.getX() + ","+event.getY());
                break;
            // ドラッグしている
            case MotionEvent.ACTION_MOVE:
                break;
            // 離された
            case MotionEvent.ACTION_UP:
                final int x1 = downX;
                final int y1 = downY;
                final int x2 = (int) event.getX();
                final int y2 = (int) event.getY();
                Log.i("act","xy1 = "+ downX + ","+downY);
                Log.i("act","xy2 = "+event.getX() + ","+event.getY());

                // 一定時間待って、次の画面に遷移する
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Intent intent = new Intent(sContext, NfcWriteActivity.class);
                        intent.putExtra(AppUtils.INTENT_EXTRA_NFC_TYPE, AppUtils.NFC_TYPE_TOUCH);
                        intent.putExtra(AppUtils.INTENT_EXTRA_FINGER, mFinger);
                        intent.putExtra(AppUtils.INTENT_EXTRA_TOUCH_X, x1);
                        intent.putExtra(AppUtils.INTENT_EXTRA_TOUCH_Y, y1);
                        intent.putExtra(AppUtils.INTENT_EXTRA_TOUCH_END_X, x2);
                        intent.putExtra(AppUtils.INTENT_EXTRA_TOUCH_END_Y, y2);
                        startActivity(intent);
                        finish();
                    }
                }.execute();
                Toast.makeText(this, "NFC書き込み準備を行います", Toast.LENGTH_LONG).show();

                break;
        }
        return false;
    }

    /**
     * OptionsMenu をクリックしたとき
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        Log.i(TAG, "Selected menu: " + menu.getItemId() + ", " + menu.getTitle());
        switch (menu.getItemId()) {
            case OPTIONS_ITEM_ID_UP:
                mCurFileNum++;
                if (mCurFileNum > mFileList.length - 1) {
                    mCurFileNum = mFileList.length-1;
                }
                break;
            case OPTIONS_ITEM_ID_DOWN:
                mCurFileNum--;
                if (mCurFileNum < 0) {
                    mCurFileNum = 0;
                }
                break;
        }
        Drawable drawable = Drawable.createFromPath(mFileList[mCurFileNum].getPath());
        mBackImageView.setImageDrawable(drawable);
        return false;
    }
}
