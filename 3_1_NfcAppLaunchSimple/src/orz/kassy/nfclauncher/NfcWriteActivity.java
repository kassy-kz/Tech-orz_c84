
package orz.kassy.nfclauncher;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

public class NfcWriteActivity extends Activity {
    private String TAG;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mWriteTagFilters;
    EditText mNote;
    private String mPackage;

    /**
     * Activity生成時に呼ばれる
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // レイアウトの処理
        setContentView(R.layout.activity_sub);
        Button btn = (Button) findViewById(R.id.btn);

        // 書き込むパッケージ名をIntentから取得
        mPackage = getIntent().getStringExtra("package");
        btn.setText(mPackage);
    }

    // Activity表示時に呼ばれる
    @Override
    protected void onResume() {
        super.onResume();
        enableTagWriteMode();
    }

    /**
     * タグを書き込める状態にする
     */
    private void enableTagWriteMode() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    /**
     * NFCを近づけるとこれが呼ばれる
     */
    @Override
    protected void onNewIntent(Intent intent) {

        // NFCタグを発見した場合
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            // IntentからNFCタグを取得して、
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // NdefRecord(ApplicationRecored)生成
            NdefRecord aplRecord = NdefRecord.createApplicationRecord(mPackage);

            // NdefMessage生成
            NdefMessage msg = new NdefMessage(new NdefRecord[] {
                aplRecord
            });
            
            // 書き込み実施
            writeTag(msg, detectedTag);
        }
    }

    /**
     * NFCタグにNDEFを書き込む
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
                    toast("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    toast("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                toast("Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        toast("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        toast("Failed to format tag.");
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

    /**
     * Toastを表示する
     * @param text
     */
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
