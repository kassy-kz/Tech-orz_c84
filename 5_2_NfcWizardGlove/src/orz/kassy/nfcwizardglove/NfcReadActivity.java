
package orz.kassy.nfcwizardglove;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


public class NfcReadActivity extends Activity {

    private TimerTask mTimer;
    private Handler mHandler;
    private int mTouchX, mTouchY, mTouchEndX, mTouchEndY;
    private static Context sContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_read);
        mHandler = new Handler();
        mHandler.postDelayed(showMessageTask, 700);
        sContext = this;
        
        Uri uri = getIntent().getData();
        if(uri.getHost().equals(AppUtils.URI_HOST_TOUCH)) {
            mTouchX = Integer.parseInt(uri.getQueryParameter(AppUtils.URI_PARAM_X));
            mTouchY = Integer.parseInt(uri.getQueryParameter(AppUtils.URI_PARAM_Y));
            mTouchEndX = Integer.parseInt(uri.getQueryParameter(AppUtils.URI_PARAM_END_X));
            mTouchEndY = Integer.parseInt(uri.getQueryParameter(AppUtils.URI_PARAM_END_Y));
        }
        
        // カスタマイズしたトーストを表示（チョーイイネ）
        showCustomToast();
        
        // ActivityをfinishしてからBroadcastを投げるようにする
        finish();
    }
    
    private void showCustomToast() {
     // インフレータを取得する
        LayoutInflater inflater = getLayoutInflater();
         
        // カスタムToast用のViewを取得する
        View layout = inflater.inflate(R.layout.toast_custom, null);
        // Toastにカスタマイズしたレイアウトを設定して表示する
        Toast toast = new Toast(this);
        toast.setView(layout);
        toast.show();
         
    }

    private final Runnable showMessageTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            if((Math.abs(mTouchX-mTouchEndX) + Math.abs(mTouchEndY-mTouchY) > 30)) {
                intent.setAction("orz.kassy.nfcwizard.slideevent");                
            } else {
                intent.setAction("orz.kassy.nfcwizard.tapevent");                                
            }
            intent.putExtra("x", mTouchX);
            intent.putExtra("y", mTouchY);
            intent.putExtra("x2", mTouchEndX);
            intent.putExtra("y2", mTouchEndY);
            sendBroadcast(intent);
        }
    };    
}
