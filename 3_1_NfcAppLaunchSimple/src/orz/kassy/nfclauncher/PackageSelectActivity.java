
package orz.kassy.nfclauncher;

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
    
    /**
     * Activity生成時に呼ばれる
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // レイアウト
        setContentView(R.layout.activity_package_select);       
        mAppListView = (ListView) findViewById(R.id.app_list);
        
        // インストールされているアプリ一覧取得
        mPackageManager = getPackageManager();
        mInfos = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        
        // アプリ一覧をリストに表示
        AppInfoAdapter adapter = new AppInfoAdapter(this, mInfos);
        mAppListView.setAdapter(adapter);
        mAppListView.setOnItemClickListener(this);
    }

    // リストのアイテムがクリックされた時
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // パッケージ情報をIntentに格納して
        Intent intent = new Intent(PackageSelectActivity.this, NfcWriteActivity.class);
        intent.putExtra("package", mInfos.get(position).packageName);

        // 画面遷移する
        startActivity(intent);
    }

    /**
     * Application情報を表示するアダプター
     */
    public class AppInfoAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private int mViewResourceId;
        private List<ApplicationInfo> mList;
        
        public AppInfoAdapter(Context context , List<ApplicationInfo> list) {
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
