
package orz.kassy.nfcwizardglove;

import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener , DialogInterface.OnClickListener {

    private static final int DIALOG_LIST_ACTION = 1;
    private static int sSelectedFinger;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ActionBarをセット
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new SectionFragment();
            Bundle args = new Bundle();
            args.putInt(SectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * 中のセクションのフラグメント（左手、右手）
     */
    public static class SectionFragment extends Fragment implements OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        private static final String TAG = null;
        private Button mBtnc1;
        private ImageView mIv1;
        private Button mBtnc2;
        private ImageView mIv2;
        private Button mBtnc3;
        private ImageView mIv3;
        private Button mBtnc4;
        private ImageView mIv4;
        private Button mBtnc5;
        private ImageView mIv5;

        public SectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            //　レイアウト全体
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            // ハンドの画像
            ImageView iv = (ImageView) rootView.findViewById(R.id.hand_image);
            Log.i(TAG,"arg = "+getArguments().getInt(ARG_SECTION_NUMBER));
            // 左手表示時
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                iv.setImageResource(R.drawable.left_hand2);
            // 右手表示時
            } else {
                iv.setImageResource(R.drawable.right_hand2);                                
            }
            
            // その他
            mBtnc1 = (Button) rootView.findViewById(R.id.btn_command1); 
            mBtnc1.setOnClickListener(this);
            mIv1 = (ImageView) rootView.findViewById(R.id.icon_command1);
            mBtnc2 = (Button) rootView.findViewById(R.id.btn_command2); 
            mBtnc2.setOnClickListener(this);
            mIv2 = (ImageView) rootView.findViewById(R.id.icon_command2);
            mBtnc3 = (Button) rootView.findViewById(R.id.btn_command3); 
            mBtnc3.setOnClickListener(this);
            mIv3 = (ImageView) rootView.findViewById(R.id.icon_command3);
            mBtnc4 = (Button) rootView.findViewById(R.id.btn_command4); 
            mBtnc4.setOnClickListener(this);
            mIv4 = (ImageView) rootView.findViewById(R.id.icon_command4);
            mBtnc5 = (Button) rootView.findViewById(R.id.btn_command5); 
            mBtnc5.setOnClickListener(this);
            mIv5 = (ImageView) rootView.findViewById(R.id.icon_command5);
            
            return rootView;
        }
        
        /**
         * フラグメントが表示されるときに呼ばれる
         */
        @Override
        public void onResume() {
            super.onResume();
            // 左手表示時
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                // 文字列をボタンに表示する
                String str1 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_LEFT_THUMB);
                mBtnc1.setText(str1);
                // アイコンを表示する
                Drawable drw1 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_LEFT_THUMB);
                mIv1.setImageDrawable(drw1);            

                // 文字列をボタンに表示する
                String str2 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_LEFT_FORE);
                mBtnc2.setText(str2);
                // アイコンを表示する
                Drawable drw2 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_LEFT_FORE);
                mIv2.setImageDrawable(drw2);            

                // 文字列をボタンに表示する
                String str3 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_LEFT_MID);
                mBtnc3.setText(str3);
                // アイコンを表示する
                Drawable drw3 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_LEFT_MID);
                mIv3.setImageDrawable(drw3);            

                // 文字列をボタンに表示する
                String str4 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_LEFT_ANNULAR);
                mBtnc4.setText(str4);
                // アイコンを表示する
                Drawable drw4 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_LEFT_ANNULAR);
                mIv4.setImageDrawable(drw4);            

                // 文字列をボタンに表示する
                String str5 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_LEFT_LITTLE);
                mBtnc5.setText(str5);
                // アイコンを表示する
                Drawable drw5 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_LEFT_LITTLE);
                mIv5.setImageDrawable(drw5);            
            // 右手表示時
            } else {
                // 文字列をボタンに表示する
                String str1 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_RIGHT_THUMB);
                mBtnc1.setText(str1);
                // アイコンを表示する
                Drawable drw1 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_RIGHT_THUMB);
                mIv1.setImageDrawable(drw1);            

                // 文字列をボタンに表示する
                String str2 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_RIGHT_FORE);
                mBtnc2.setText(str2);
                // アイコンを表示する
                Drawable drw2 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_RIGHT_FORE);
                mIv2.setImageDrawable(drw2);            

                // 文字列をボタンに表示する
                String str3 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_RIGHT_MID);
                mBtnc3.setText(str3);
                // アイコンを表示する
                Drawable drw3 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_RIGHT_MID);
                mIv3.setImageDrawable(drw3);            

                // 文字列をボタンに表示する
                String str4 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_RIGHT_ANNULAR);
                mBtnc4.setText(str4);
                // アイコンを表示する
                Drawable drw4 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_RIGHT_ANNULAR);
                mIv4.setImageDrawable(drw4);            

                // 文字列をボタンに表示する
                String str5 = AppUtils.loadCommandAsString(getActivity(), AppUtils.FINGER_RIGHT_LITTLE);
                mBtnc5.setText(str5);
                // アイコンを表示する
                Drawable drw5 = AppUtils.loadCommandIcon(getActivity(),AppUtils.FINGER_RIGHT_LITTLE);
                mIv5.setImageDrawable(drw5);            
            }
        }


        /**
         * ボタンが押された
         */
        @Override
        public void onClick(View v) {
            if(getArguments().getInt(ARG_SECTION_NUMBER) ==1) { 
                switch (v.getId()) {
                    case R.id.btn_command1:
                        sSelectedFinger = AppUtils.FINGER_LEFT_THUMB;
                        break;
                    case R.id.btn_command2:
                        sSelectedFinger = AppUtils.FINGER_LEFT_FORE;                        
                        break;
                    case R.id.btn_command3:
                        sSelectedFinger = AppUtils.FINGER_LEFT_MID;
                        break;
                    case R.id.btn_command4:
                        sSelectedFinger = AppUtils.FINGER_LEFT_ANNULAR;
                        break;
                    case R.id.btn_command5:
                        sSelectedFinger = AppUtils.FINGER_LEFT_LITTLE;
                        break;
                    default:
                        break;
                }
            } else {
                switch (v.getId()) {
                    case R.id.btn_command1:
                        sSelectedFinger = AppUtils.FINGER_RIGHT_THUMB;
                        break;
                    case R.id.btn_command2:
                        sSelectedFinger = AppUtils.FINGER_RIGHT_FORE;                        
                        break;
                    case R.id.btn_command3:
                        sSelectedFinger = AppUtils.FINGER_RIGHT_MID;
                        break;
                    case R.id.btn_command4:
                        sSelectedFinger = AppUtils.FINGER_RIGHT_ANNULAR;
                        break;
                    case R.id.btn_command5:
                        sSelectedFinger = AppUtils.FINGER_RIGHT_LITTLE;
                        break;
                    default:
                        break;
                }                
            }
            getActivity().showDialog(DIALOG_LIST_ACTION);
        }
    }
    
    /**
     * ダイアログ生成処理
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog listDialog = new AlertDialog.Builder(this)
        .setTitle(R.string.action_command_type)
        .setItems(R.array.select_command_type, this)
        .create();
        return listDialog;
    }

    /**
     * ダイアログのボタンが押された
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent();
        intent.putExtra(AppUtils.INTENT_EXTRA_FINGER, sSelectedFinger);
        switch(which){
            // アプリケーション起動、が押された
            case 0:
                intent.setClass(this, PackageSelectActivity.class);
                startActivity(intent);
                break;
            // タッチイベント取得、が押された
            case 1:
                intent.setClass(this, SetTouchEventActivity.class);
                startActivity(intent);
                break;
            case 2:
                break;
        }       
    }
}