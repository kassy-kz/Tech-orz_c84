
package orz.kassy.gmapintent;

import java.lang.reflect.Method;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

  private static final String TAG = null;
  private int mKeyCode;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toast.makeText(this, "ver3", 5).show();
  
    Button btn1 = (Button) findViewById(R.id.test1);
    btn1.setOnClickListener(this);
    Button btn2 = (Button) findViewById(R.id.test2);
    btn2.setOnClickListener(this);
    Button btn3 = (Button) findViewById(R.id.test3);
    btn3.setOnClickListener(this);
    Button btn4 = (Button) findViewById(R.id.test4);
    btn4.setOnClickListener(this);
    
    Button btn5 = (Button) findViewById(R.id.test5);
    btn5.setOnClickListener(this);
    Button btn6 = (Button) findViewById(R.id.test6);
    btn6.setOnClickListener(this);
    Button btn7 = (Button) findViewById(R.id.test7);
    btn7.setOnClickListener(this);
    Button btn8 = (Button) findViewById(R.id.test8);
    btn8.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.test1:
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +35.41+","+139.42));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        break;
      case R.id.test2:
        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q='東京駅'"));
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent2);
        break;
      case R.id.test3:
        Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q='東京駅'&mode=w"));
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent3);
        break;
      case R.id.test4:
 //       Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:q='東京駅'&z=23"));
        Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:?q=大阪駅"));       
        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent4);
        break;
        
      case R.id.test5:
        
        Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:?q='東京駅'&mode=w"));
        intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent5);
        
        break;
      case R.id.test6:
        Intent intent6 = new Intent();
        intent6.setAction(Intent.ACTION_VIEW);
        String mapurl = "geo:38.230844,140.323316";
        intent6.setData(Uri.parse(mapurl));
        startActivity(intent6);
        break;
      case R.id.test7:

        Intent intent7 = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:?q='コンビニ'&mode=w&z=16"));
        intent7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent7);
        
        break;
      case R.id.test8:
        break;

    }
  }  
}
