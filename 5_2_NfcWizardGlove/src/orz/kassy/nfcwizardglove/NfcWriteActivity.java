
package orz.kassy.nfcwizardglove;

import java.io.IOException;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import orz.kassy.nfcwizardglove.AppUtils.*;

public class NfcWriteActivity extends Activity implements OnClickListener {
    private String TAG;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mWriteTagFilters;
    EditText mNote;
    private TextView mTextView;
    private Uri mWriteUri;
    private NdefMessage mNdefMessage = null;
    private Vibrator vib;
    private int mFinger;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // バイブ
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
       
        // レイアウト
        setContentView(R.layout.activity_nfc_write);
        mTextView = (TextView) findViewById(R.id.textviewNfcWriteMain);
        Button btn = (Button) findViewById(R.id.backButton);
        btn.setOnClickListener(this);
        
        // ペンディングインテント
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // どの指かを取得
        mFinger = getIntent().getIntExtra(AppUtils.INTENT_EXTRA_FINGER, 100);

        // NDEFを生成する
        createWriteNdefMessage();
    }
    
    /**
     * 書き込みNdefMessageを生成する
     */
    private void createWriteNdefMessage() {
        Intent intent = getIntent();
        switch(intent.getIntExtra(AppUtils.INTENT_EXTRA_NFC_TYPE, 0)) {
            case AppUtils.NFC_TYPE_APPLICATION:
                String packageName = intent.getStringExtra(AppUtils.INTENT_EXTRA_PACKAGE_NAME);
                mTextView.setText(packageName);
                
                // NdefRecord生成
                NdefRecord packageRecord = NdefRecord.createApplicationRecord(packageName);

                // NdefMessage生成
                mNdefMessage = new NdefMessage(new NdefRecord[] {
                        packageRecord
                });         
                
                // 情報を設定保存する
                AppUtils.saveCommand(this, mFinger, AppUtils.NFC_TYPE_APPLICATION, packageName);


                break;
            case AppUtils.NFC_TYPE_TOUCH:
                // URI作成する
                int touchX = intent.getIntExtra(AppUtils.INTENT_EXTRA_TOUCH_X, 0);
                int touchY = intent.getIntExtra(AppUtils.INTENT_EXTRA_TOUCH_Y, 0);
                int endX = intent.getIntExtra(AppUtils.INTENT_EXTRA_TOUCH_END_X,0);
                int endY = intent.getIntExtra(AppUtils.INTENT_EXTRA_TOUCH_END_Y,0);
                mWriteUri = Uri.parse(AppUtils.URI_SCHEME
                                    +"://" + AppUtils.URI_HOST_TOUCH 
                                    +"/?" + AppUtils.URI_PARAM_X +"="+ touchX
                                    +"&" +  AppUtils.URI_PARAM_Y + "="+ touchY
                                    +"&" +  AppUtils.URI_PARAM_END_X + "="+ endX
                                    +"&" +  AppUtils.URI_PARAM_END_Y + "="+ endY);
                mTextView.setText(mWriteUri.toString());
                
                // 情報を設定保存する
                AppUtils.saveCommand(this, mFinger, AppUtils.NFC_TYPE_TOUCH, mWriteUri.toString());
                
                // NdefRecord生成
                NdefRecord uriRecord = NdefRecord.createUri(mWriteUri.toString());

                // NdefMessage生成
                mNdefMessage = new NdefMessage(new NdefRecord[] {
                        uriRecord
                });

                break;
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableTagWriteMode();
//        Toast.makeText(this, "NFC書き込み準備が整いました", Toast.LENGTH_LONG).show();
    }

    /**
     * NFC書き込み可能状態にする
     */
    private void enableTagWriteMode() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    /**
     * NFCタグが近づいたら呼ばれる
     */
    @Override
    protected void onNewIntent(Intent intent) {

        // NFCタグを発見した場合
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            // IntentからNFCタグを取得して、
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if(mNdefMessage != null) {
                writeTag(mNdefMessage, detectedTag);
            }
        }
    }

    /**
     * NFC書き込みを実施する
     * @param message
     * @param tag
     * @return
     */
    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
//                    toast("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
//                    toast("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
//                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                Toast.makeText(this, "NFC書き込みが完了しました", Toast.LENGTH_LONG).show();
                //toast("Wrote message to pre-formatted tag.");
                vib.vibrate(300);

                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
//                        toast("Formatted tag and wrote message");
                        Toast.makeText(this, "NFC書き込みが完了しました", Toast.LENGTH_LONG).show();
                        vib.vibrate(300);
                        return true;
                    } catch (IOException e) {
//                        toast("Failed to format tag.");
                        return false;
                    }
                } else {
                    toast("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            toast("Failed to write tag");
        }

        return false;
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;

            default:
                break;
        }
    }

}
