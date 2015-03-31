package pl.tajchert.cheatwear;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

public class UpdateService extends Service {
    private static final String TAG = UpdateService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent !=null && intent.getExtras()!=null) {
            String value = intent.getExtras().getString(Tools.SERVICE_KEY_TEXT);
            new SendTextToWatch().execute(value);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private class SendTextToWatch extends AsyncTask<String, Void, String> {
        private GoogleApiClient mGoogleAppiClient;
        @Override
        protected String doInBackground(String... params) {
            return  params[0];
        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null){
                sendData(Tools.WEAR_KEY_CHEAT_TEXT, UpdateService.this.getString(R.string.send_error));
                return;
            }
            sendData(Tools.WEAR_KEY_CHEAT_TEXT, result);
        }
        @Override
        protected void onPreExecute() {
            mGoogleAppiClient = new GoogleApiClient.Builder(UpdateService.this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                        }
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }
                    })
                    .addApi(Wearable.API)
                    .build();
            mGoogleAppiClient.connect();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        private void sendData(String key, String value) {
            if(value == null || value.length() < 3){
                //Empty string, do not send, 3 is for 'hot' word
                return;
            }
            value = value +  Calendar.getInstance().getTimeInMillis();
            PutDataMapRequest dataMap = PutDataMapRequest.create(Tools.WEAR_PATH);
            dataMap.getDataMap().putString(key, value);
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleAppiClient, request);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sent: " + dataItemResult);
                    mGoogleAppiClient.disconnect();
                    stopSelf();
                }
            });

        }
    }


}
