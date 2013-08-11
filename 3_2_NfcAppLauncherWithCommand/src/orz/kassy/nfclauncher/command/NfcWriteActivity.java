
package orz.kassy.nfclauncher.command;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextView mTextView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        setContentView(R.layout.activity_sub);
        mTextView1 = (TextView) findViewById(R.id.textView1);
        
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toast(getIntent().getAction());

        // NFC書き込みモードにする
        enableTagWriteMode();

        if (!getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            return;
        }

        // NFCを読み込む
        readNdefRecord(getIntent());

    }

    /**
     * NFCタグを読み込む処理、onResumeかonNewIntentから呼び出す
     */
    private void readNdefRecord(Intent intent) {

        // NFCの中身を取得する（インテントに複数NdefMessageがあることがある）
        Parcelable[] rawMessages;
        rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages == null) {
            // 中身のないタグです。
            mTextView1.setText("NFCがよめません");
            return;
        }

        // Parcelable型からNdefMessage型に入れ直す。
        NdefMessage[] msgs = new NdefMessage[rawMessages.length];

        for (int i = 0; i < rawMessages.length; i++) {
            msgs[i] = (NdefMessage) rawMessages[i];

            // NdefMessageからNdefRecordを取り出す
            for (NdefRecord record : msgs[i].getRecords()) {

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

                    // payloadの確認
                    mTextView1.setText("type : " + typeStr +"\n payload : "+ payloadStr);
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
     * タグが近づけられた時に呼ばれる
     */
    @Override
    protected void onNewIntent(Intent intent) {
        toast("Tag get. : " + intent.getAction());

        // Tag writing mode
        if (NfcAdapter.ACTION_TAG_DISCOVERED
                .equals(intent.getAction())) {
            // Intentからタグを取得して、
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // タグにEditTextの内容を書き込み。
            NdefRecord appRecord = NdefRecord.createApplicationRecord("orz.kassy.nfclauncher.command");
            NdefRecord exRecord = NdefRecord.createExternal("orz.kassy.nfclauncher.command", "type_name",
                    "type_payload".getBytes());
            NdefMessage msg = new NdefMessage(new NdefRecord[] {
                    appRecord, exRecord
            });
            writeTag(msg, detectedTag);
        }
    }

    /**
     * NFCにNDEFを書き込む処理
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
     * 
     * @param text
     */
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // byte配列をStringにして返す
    public String bytesToString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        boolean isFirst = true;

        for (byte b : bytes) {
            if (isFirst) {
                isFirst = false;
            } else {
                buffer.append("-");
            }
            buffer.append(Integer.toString(b & 0xff));
        }
        return buffer.toString();
    }

    // byte配列をInt配列にして返す
    public int[] byteToInt(byte[] bytes) {
        int[] result = new int[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i] & 0xff;
        }
        return result;
    }
}
