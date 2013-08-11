
package orz.kassy.nfcwizardglove;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PackageSelectActivity extends Activity implements OnItemClickListener {

    private ListView mAppListView;
    PackageManager mPackageManager;
    private List<ApplicationInfo> mInfos;
    private int mFinger;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_select);
       
        mAppListView = (ListView) findViewById(R.id.app_list);
        
        // アプリ一覧
        mPackageManager = getPackageManager();
        mInfos = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        ApplicationInfoAdapter adapter = new ApplicationInfoAdapter(this, mInfos);
        mAppListView.setAdapter(adapter);
        
        // どの指かを取得
        mFinger = getIntent().getIntExtra(AppUtils.INTENT_EXTRA_FINGER, 1);

        mAppListView.setOnItemClickListener(this);
    }

    /**
     *　パッケージのリストが押下されたとき
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(PackageSelectActivity.this, NfcWriteActivity.class);
        intent.putExtra(AppUtils.INTENT_EXTRA_NFC_TYPE, AppUtils.NFC_TYPE_APPLICATION);
        intent.putExtra(AppUtils.INTENT_EXTRA_PACKAGE_NAME, mInfos.get(position).packageName);
        intent.putExtra(AppUtils.INTENT_EXTRA_FINGER, mFinger);

        startActivity(intent);
        finish();
    }

    /**
     * ApplicationInfoをリストに表示するためのアダプター（インナークラス）
     */
    public class ApplicationInfoAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private int mViewResourceId;
        private List<ApplicationInfo> mList;
        
        public ApplicationInfoAdapter(Context context , List<ApplicationInfo> list) {
            mList = list;
            mViewResourceId = R.layout.icon_list_row;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view;
            if(convertView !=null){
                view = convertView;
            }else{
                view = inflater.inflate(mViewResourceId,null);
            }

            ApplicationInfo item = mList.get(position);
            
            ImageView imageView = (ImageView)view.findViewById(R.id.appIcon);
            imageView.setImageDrawable(item.loadIcon(mPackageManager));
            
            TextView textView = (TextView)view.findViewById(R.id.appText1);
            textView.setText(item.loadLabel(mPackageManager));
            TextView textView2 = (TextView)view.findViewById(R.id.appText2);
            textView2.setText(item.packageName);

            return view;
        }


        @Override
        public int getCount() {
            return mList.size();
        }


        @Override
        public ApplicationInfo getItem(int position) {
            return mList.get(position);
        }


        @Override
        public long getItemId(int arg0) {
            return 0;
        }
    }

}
