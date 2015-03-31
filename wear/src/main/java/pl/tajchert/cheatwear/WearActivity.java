package pl.tajchert.cheatwear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

public class WearActivity extends Activity {
    private static final String TAG = "WearActivity";

    private BroadcastReceiver dataChangedReceiver;
    private IntentFilter dataChangedIntentFilter;

    private TextView mTextView;
    private ScrollView scrollView;
    private AnalogClock analogClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        dataChangedIntentFilter = new IntentFilter(Tools.DATA_CHANGED_ACTION);
        dataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Tools.DATA_CHANGED_ACTION.equals(intent.getAction())) {
                    String cheatText = WearActivity.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_CHEAT_TEXT, "got null");
                    if(cheatText == null){
                        sendNotificationToMobile();
                        return;
                    }
                    mTextView.setText(cheatText);
                }
            }
        };

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                analogClock = (AnalogClock) stub.findViewById(R.id.clock);
                mTextView.setText(getResources().getString(R.string.cheat_content));
                scrollView = (ScrollView) stub.findViewById(R.id.scrollView);
                //String cheatText = WearActivity.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_CHEAT_TEXT, "got null");
                //mTextView.setText(cheatText);
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //WearActivity.this.finish();
                        scrollView.setVisibility(View.INVISIBLE);
                        analogClock.setVisibility(View.VISIBLE);
                    }
                });
                analogClock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        analogClock.setVisibility(View.INVISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(dataChangedReceiver, dataChangedIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(dataChangedReceiver);
    }

    private void sendNotificationToMobile(){
        //Send empty string to ask phone to refresh weather data
        Log.d(TAG, "sendNotificationToMobile ");
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
        String value = "update_request";
        value = value +  Calendar.getInstance().getTimeInMillis();
        PutDataMapRequest dataMap = PutDataMapRequest.create(Tools.WEAR_PATH_ACTION_UPDATE);
        dataMap.getDataMap().putString(Tools.WEAR_ACTION_UPDATE, value);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "Sent: " + dataItemResult.toString());
                googleApiClient.disconnect();
            }
        });
    }
}
