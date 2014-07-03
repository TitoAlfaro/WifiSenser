package mit.edu.obmg.wifisenser;

import java.util.Arrays;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class WifiSenserMain extends ActionBarActivity {
	final String TAG = "WiFiSenserMain";

	WifiManager mainWifiObj;
	WifiScanReceiver wifiReceiver;
	ListView list;
	String[] wifis;
	int[] levels;
	int	strongestLevel;
	
	//UI
	TextView strengthLevel;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wifisensermain);
        
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        strengthLevel = (TextView) findViewById(R.id.Strength);

        mainWifiObj.startScan();

        startService(new Intent(this, WifiSenserIOIO.class).putExtra("level", strongestLevel));
    }

    protected void onPause(){
    	unregisterReceiver(wifiReceiver);
    	super.onPause();
    }
    
    protected void onResume(){
    	registerReceiver(wifiReceiver, new IntentFilter(
    			WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    	super.onResume();
    }

    protected void onStop(){
        stopService(new Intent(this, WifiSenserIOIO.class));
    	super.onStop();
    }
    
    public void StartScaning(){
    	while(true){
    		
    	}
    }
    class WifiScanReceiver extends BroadcastReceiver{
    	@SuppressLint ("UseValueOf")
    	public void onReceive(Context c, Intent intent){ 
    		
    		List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
    		wifis = new String [wifiScanList.size()];
    		levels = new int [wifiScanList.size()];
    		for(int i = 0; i<wifiScanList.size(); i++){
    			wifis[i] = ((wifiScanList.get(i)).toString());
    			levels[i]= (wifiScanList.get(i).level);
    		}
    		
    		Arrays.sort(levels);
    		strongestLevel = levels[0];
    		strengthLevel.setText("Strongest Level: " + levels[0]);
    		
            mainWifiObj.startScan();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wifi_senser_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wifisensermain, container, false);
            return rootView;
        }
    }
   
}
