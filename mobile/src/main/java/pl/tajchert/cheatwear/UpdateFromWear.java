package pl.tajchert.cheatwear;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;


public class UpdateFromWear extends WearableListenerService {
    private static final String TAG = "UpdateFromWear";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (Tools.WEAR_PATH_ACTION_UPDATE.equals(path)) {
                Intent mIntent = new Intent(this, UpdateService.class);
                mIntent.getExtras().putString(Tools.SERVICE_KEY_TEXT, "Run app on the phone, and set cheat text.");
                UpdateFromWear.this.startService(mIntent);
            }
        }
    }
}
