package orz.kassy.nfcwizardglove;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

public class AppUtils {

    public static final String INTENT_EXTRA_FINGER = "finger";
    public static final String INTENT_EXTRA_HAND = "hand";

    public static final int NFC_TYPE_TOUCH = 1;
    public static final int NFC_TYPE_APPLICATION = 2;
    
    public static final String INTENT_EXTRA_NFC_TYPE = "intent_extra_nfc_type";
    public static final String INTENT_EXTRA_TOUCH_X = "intent_x";
    public static final String INTENT_EXTRA_TOUCH_Y = "intent_y";
    public static final String INTENT_EXTRA_TOUCH_END_X = "intent_end_x";
    public static final String INTENT_EXTRA_TOUCH_END_Y = "intent_end_y";

    public static final String INTENT_EXTRA_PACKAGE_NAME = "package";

    public static final String URI_SCHEME = "nfcwizard";
    public static final String URI_HOST_TOUCH = "touch";
    public static final String URI_PARAM_X = "x";
    public static final String URI_PARAM_Y = "y";
    public static final String URI_PARAM_END_X = "x2";
    public static final String URI_PARAM_END_Y = "y2";

    public static final String PREF_FILE_NAME = "pref_file";

    private static final String SH_COMMAND = "sh_command";
    private static final String SH_VALUE = "sh_value";
    
    public static final int FINGER_LEFT_THUMB = 0;
    public static final int FINGER_LEFT_FORE = 1;
    public static final int FINGER_LEFT_MID = 2;
    public static final int FINGER_LEFT_ANNULAR = 3;
    public static final int FINGER_LEFT_LITTLE = 4;
    public static final int FINGER_RIGHT_THUMB = 5;
    public static final int FINGER_RIGHT_FORE = 6;
    public static final int FINGER_RIGHT_MID = 7;
    public static final int FINGER_RIGHT_ANNULAR = 8;
    public static final int FINGER_RIGHT_LITTLE = 9;
    private static PackageManager mPackageManager;
    private static List<ApplicationInfo> mInfos;


    public static void saveCommand(Context context, int finger, int command, String value) {
        SharedPreferences shPref = context.getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        Editor e = shPref.edit();
        String key_command = SH_COMMAND + finger ;
        String key_value = SH_VALUE + finger;
        e.putInt(key_command, command);
        e.putString(key_value, value);
        e.commit();
    }
    
    /**
     * コマンドを取得、そのままNdefMessageにして取得
     * @param context
     * @param finger
     * @return
     */
    public static NdefMessage loadCommandAsNdefMessage(Context context, int finger) {
        SharedPreferences shPref = context.getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        String key_command = SH_COMMAND + finger ;
        String key_value = SH_VALUE + finger;

        int pref_command = shPref.getInt(key_command, 0);
        String pref_value  = shPref.getString(key_value, "");
        NdefMessage ndefMessage;
        
        switch (pref_command) {
            case NFC_TYPE_APPLICATION:
                // NdefRecord生成
                NdefRecord packageRecord = NdefRecord.createApplicationRecord(pref_value);

                // NdefMessage生成
                ndefMessage = new NdefMessage(new NdefRecord[] {
                        packageRecord
                });                
                return ndefMessage;
                        
            case NFC_TYPE_TOUCH:
                NdefRecord uriRecord = NdefRecord.createUri(pref_value);
                // NdefMessage生成
                ndefMessage = new NdefMessage(new NdefRecord[] {
                        uriRecord
                });                
                return ndefMessage;
                    
            default:
                return null;
        } 
    }

    /**
     * コマンドを文字列として取得する
     * @param context
     * @param finger
     * @return
     */
    public static String loadCommandAsString(Context context, int finger) {
        SharedPreferences shPref = context.getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        String key_value = SH_VALUE + finger;

        String pref_value  = shPref.getString(key_value, "miss");
        return pref_value;        
    }

    public static Drawable loadCommandIcon(Context context, int finger) {
        SharedPreferences shPref = context.getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        String key_command = SH_COMMAND + finger ;
        String key_value = SH_VALUE + finger;

        int pref_command = shPref.getInt(key_command, 0);
        //String pref_value  = shPref.getString(key_value, "");
        NdefMessage ndefMessage;
        
        switch (pref_command) {
            case NFC_TYPE_APPLICATION:
                String package_name  = shPref.getString(key_value, "");
                // アプリ一覧
                mPackageManager = context.getPackageManager();
                mInfos = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for(ApplicationInfo info : mInfos) {
                    if(info.packageName.equals(package_name)) {
                        return info.loadIcon(mPackageManager);
                    }
                }
                return null;
            case NFC_TYPE_TOUCH:
                String uri_value =  shPref.getString(key_value, "");
                return context.getResources().getDrawable(R.drawable.one_finger);
        }
        return null;
    }

}
