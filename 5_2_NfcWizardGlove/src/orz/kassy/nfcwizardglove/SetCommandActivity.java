package orz.kassy.nfcwizardglove;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class SetCommandActivity extends SherlockActivity implements ActionBar.OnNavigationListener {
    
    private static final String TAG = null;
    private TextView mSelected;
    private String[] mLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light); 
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_navigation);
        mSelected = (TextView)findViewById(R.id.text);

        mLocations = getResources().getStringArray(R.array.command);

        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.command, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        
        int hand = getIntent().getIntExtra(AppUtils.INTENT_EXTRA_HAND, 1);
        int finger = getIntent().getIntExtra(AppUtils.INTENT_EXTRA_FINGER, 1);
        Log.i( TAG, "hand = " + hand + ", finger = "+ finger );
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mSelected.setText("Selected: " + mLocations[itemPosition]);
        return true;
    }
}
