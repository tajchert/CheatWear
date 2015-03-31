package pl.tajchert.cheatwear;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;


public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = DataLayerListenerService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            Log.d(TAG, "Update, path: " +path);
            if (Tools.WEAR_PATH.equals(path)) {
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                String cheatText = item.getDataMap().getString(Tools.WEAR_KEY_CHEAT_TEXT);
                Log.d(TAG, "got: " + cheatText);
                if(cheatText != null) {
                    cheatText = cheatText.replaceAll("\\d","");
                    if(!cheatText.equals("")){
                        getBaseContext().getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putString(Tools.PREFS_KEY_CHEAT_TEXT, cheatText).apply();
                        getBaseContext().sendBroadcast(new Intent(Tools.DATA_CHANGED_ACTION));
                    }
                }
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {}

    @Override
    public void onPeerDisconnected(Node peer){}

}
