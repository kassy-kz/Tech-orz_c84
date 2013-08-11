
package orz.kassy.simplewebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions;
import android.widget.Toast;

public class MapsV3Activity extends Activity {
    private static WebView mWebView;
    private static Activity sActivity;
    private IntentFilter[] mWriteTagFilters;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    
    /** Called when the activity is first created. */
    @SuppressLint({
            "SdCardPath", "SetJavaScriptEnabled"
    })
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = this;
        setContentView(R.layout.activity_web_view);
        mWebView = (WebView) findViewById(R.id.webview);

        // WebViewClient を設定する
        mWebView.setWebViewClient(new WebViewClient() {
        });

        // WebChromeClient を設定する
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                sActivity.setProgress(progress * 1000);
            }

            public void onGeolocationPermissionsShowPrompt(String origin,
                    GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        // デフォルトのズームコントロールを使う
        mWebView.getSettings().setBuiltInZoomControls(true);

        // JavaScriptを有効にする
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);

        // HTML読み込み
        // assets以下に入っているindex.htmlをadb push して端末の /sdcard/に格納して使います
        mWebView.loadUrl("file:///sdcard/index.html");
        
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // NFC書き込みモードにする
        enableTagWriteMode();

        if (!getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            return;
        }

        // NFCを読み込む
        readNdefRecord(getIntent());

    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        toast("Tag get. : " + intent.getAction());
        readNdefRecord(intent);
    }
    
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * NFCタグを読み込む処理、onResumeかonNewIntentから呼び出す
     * @param intent
     */
    private void readNdefRecord(Intent intent) {

        // NFCの中身を取得する（インテントに複数NdefMessageがあることがある）
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages == null) {
            return;
        }

        for (int i = 0; i < rawMessages.length; i++) {
            // NdefMessage型に格納
            NdefMessage ndefMsg = (NdefMessage) rawMessages[i];

            // NdefMessageからNdefRecordを取り出す
            for (NdefRecord record : ndefMsg.getRecords()) {

                // NdefRecordのTnfがExternalの場合
                if (record.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE) {
                    String payloadStr = "";
                    String typeStr = "";

                    // データ本体のType部を取り出す。
                    byte[] typeBytes = record.getType();
                    typeStr = new String(typeBytes);

                    // データ本体のPayload部を取り出す。
                    byte[] payloadBytes = record.getPayload();
                    payloadStr = new String(payloadBytes);

                    // payloadの合致確認、
                    // 都合により、ここでは超手抜き確認しているので注意
                    if (payloadStr.endsWith("a")) {
                        mapScrollUp();
                    } else if (payloadStr.endsWith("b")) {
                        mapScrollDown();
                    } else if (payloadStr.endsWith("c")) {
                        mapScrollLeft();
                    } else if (payloadStr.endsWith("d")) {
                        mapScrollRight();                        
                    } else if (payloadStr.endsWith("e")) {
                        mapScrollToMyLocation();                                                
                        mapScrollRight();                        
                    } else if (payloadStr.endsWith("f")) {
                        mapZoomIn();                        
                    } else if (payloadStr.endsWith("g")) {
                        mapZoomOut();                                                
                    }
                } else if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {

                }

            }
        }
    }

    /**
     * NFC書き込みモードにする
     */
    private void enableTagWriteMode() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    
    /**
     * 上にスクロール
     */
    public void mapScrollUp() {
        mWebView.loadUrl("javascript:scrollUp();");
    }

    /**
     * 下にスクロール
     */
    public void mapScrollDown() {
        mWebView.loadUrl("javascript:scrollDown();");
    }

    /**
     * 左にスクロール
     */
    public void mapScrollLeft() {
        mWebView.loadUrl("javascript:scrollLeft();");
    }

    /**
     * 右にスクロール
     */
    public void mapScrollRight() {
        mWebView.loadUrl("javascript:scrollRight();");
    }

    /**
     * 拡大
     */
    public void mapZoomIn() {
        mWebView.loadUrl("javascript:zoomIn();");
    }

    /**
     * 縮小
     */
    public void mapZoomOut() {
        mWebView.loadUrl("javascript:zoomOut();");
    }
    
    /**
     * これを呼び出すと地図を現在地へとスクロールさせる
     */
    public void mapScrollToMyLocation() {
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.setClass(this, NfcWriteActivity.class);
        startActivity(intent);
        return true;
    }

}
