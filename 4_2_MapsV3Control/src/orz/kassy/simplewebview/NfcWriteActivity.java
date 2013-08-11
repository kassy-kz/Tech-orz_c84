
package orz.kassy.simplewebview;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

public class NfcWriteActivity extends Activity implements OnClickListener {

    private IntentFilter[] mWriteTagFilters;
    EditText mNote;
    private String mPackage;
    private int mMode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sub);

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        Button btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(this);
        Button btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(this);
        Button btn6 = (Button) findViewById(R.id.btn6);
        btn6.setOnClickListener(this);
        Button btn7 = (Button) findViewById(R.id.btn7);
        btn7.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableTagWriteMode();
    }

    /**
     * NFC書き込み可能状態にする
     */
    private void enableTagWriteMode() {
        IntentFilter tagFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        mWriteTagFilters = new IntentFilter[] {
                tagFilter
        };
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent nfcIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, nfcIntent, mWriteTagFilters, null);
    }

    /**
     * NFCが近づけられると呼ばれる
     */
    @Override
    protected void onNewIntent(Intent intent) {
        toast("Tag get.");

        // Tag writing mode
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            toast("ACTION_TAG_DISCOVERED");

            // Intentからタグを取得して、
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // タグにEditTextの内容を書き込み。
            mPackage = "orz.kassy.simplewebview";
            NdefRecord appRecord = NdefRecord.createApplicationRecord(mPackage);

            NdefRecord extRecord;
            NdefMessage ndefMessage;
            
            // モードに応じて書き込む内容を変えてみる
            switch (mMode) {
                // 
                case 1:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "a".getBytes());
                    break;
                case 2:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "b".getBytes());
                    break;
                case 3:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "c".getBytes());
                    break;
                case 4:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "d".getBytes());
                    break;
                case 5:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "e".getBytes());
                    break;
                case 6:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "f".getBytes());
                    break;
                case 7:
                    extRecord = NdefRecord.createExternal("orz.kassy.simplewebview", "type_name", "g".getBytes());
                    break;
                default:
                    extRecord = null;
                    break;
            }

            ndefMessage = new NdefMessage(new NdefRecord[] {
                    appRecord, extRecord
            });
            writeTag(ndefMessage, detectedTag);
            toast("NFC書き込み準備ができました");
        }
    }

    /**
     * タグ書き込みを行う
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

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * ボタンが押されたとき
     */
    @Override
    public void onClick(View v) {
        // 書き込みモードをチェンジする
        switch (v.getId()) {
            case R.id.btn1:
                mMode = 1;
                break;
            case R.id.btn2:
                mMode = 2;
                break;
            case R.id.btn3:
                mMode = 3;
                break;
            case R.id.btn4:
                mMode = 4;
                break;
            case R.id.btn5:
                mMode = 5;
                break;
            case R.id.btn6:
                mMode = 6;
                break;
            case R.id.btn7:
                mMode = 7;
                break;
        }
    }
}
