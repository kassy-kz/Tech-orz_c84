
package orz.kassy.nfcmapcontroller;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapsV2Activity extends FragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000) // 5 seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private LocationClient mLocationClient;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    private static final String TAG = null;
    private GoogleMap mMap;
    private static final int SCROLL_LENGTH = 400;
    private IntentFilter[] mWriteTagFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

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
    
    
    @Override
    public void onResume() {
        super.onResume();
        toast(getIntent().getAction());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            toast("NDEF DISCOVERED");
        }
        
        // タグ書き込みモード
        enableTagWriteMode();
        readNdefRecord(getIntent());
        setUpLoctionClient();

    }

    /**
     * ロケーションクライアントのセットアップ・接続
     */
    private void setUpLoctionClient() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this, // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        toast("Tag get. : " + intent.getAction());
        readNdefRecord(intent);

    }

    /**
     * タグ書き込みモードにする
     */
    private void enableTagWriteMode() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
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
                    Log.i(TAG, "Ndef:" + i + " type = " + typeStr);

                    // データ本体のPayload部を取り出す。
                    byte[] payloadBytes = record.getPayload();
                    payloadStr = new String(payloadBytes);
                    Log.i(TAG, "Ndef:" + i + " payload : " + payloadStr);

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

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 上にスクロール
     */
    public void mapScrollUp() {
        CameraUpdate update = CameraUpdateFactory.scrollBy(0, -SCROLL_LENGTH);
        mMap.animateCamera(update, null);
    }

    /**
     * 下にスクロール
     */
    public void mapScrollDown() {
        CameraUpdate update = CameraUpdateFactory.scrollBy(0, SCROLL_LENGTH);
        mMap.animateCamera(update, null);
    }

    /**
     * 左にスクロール
     */
    public void mapScrollLeft() {
        CameraUpdate update = CameraUpdateFactory.scrollBy(-SCROLL_LENGTH, 0);
        mMap.animateCamera(update, null);
    }

    /**
     * 右にスクロール
     */
    public void mapScrollRight() {
        CameraUpdate update = CameraUpdateFactory.scrollBy(SCROLL_LENGTH, 0);
        mMap.animateCamera(update, null);
    }

    /**
     * 拡大
     */
    public void mapZoomIn() {
        CameraUpdate update = CameraUpdateFactory.zoomIn();
        mMap.animateCamera(update, null);
    }

    /**
     * 縮小
     */
    public void mapZoomOut() {
        CameraUpdate update = CameraUpdateFactory.zoomOut();
        mMap.animateCamera(update, null);
    }

    /**
     * これを呼び出すと地図を現在地へとスクロールさせる
     */
    public void mapScrollToMyLocation() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            Location location = mLocationClient.getLastLocation();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(update, null);
        }
    }

    /**
     * 現在地が変わった
     */
    @Override
    public void onLocationChanged(Location location) {
        // mapScrollToMyLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    /**
     * ロケーションクライアントに接続した
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this); // LocationListener
    }

    @Override
    public void onDisconnected() {
    }

}
