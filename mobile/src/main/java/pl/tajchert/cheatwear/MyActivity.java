package pl.tajchert.cheatwear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MyActivity extends Activity {

    private EditText textToSend;
    private Button buttonSend;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        textToSend = (EditText) findViewById(R.id.editTextToSend);
        buttonSend = (Button) findViewById(R.id.buttonSend);

        prefs = this.getSharedPreferences(Tools.PREFS, Context.MODE_PRIVATE);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save to shared prefs and send
                getSaveText();
            }
        });
    }

    private void getSaveText() {
        String text =  textToSend.getText()+"";
        prefs.edit().putString(Tools.PREFS_KEY_CHEAT_TEXT, text).apply();
        Intent mIntent = new Intent(this, UpdateService.class);
        mIntent.putExtra(Tools.SERVICE_KEY_TEXT, text);
        this.startService(mIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSaveText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textToSend.setText(prefs.getString(Tools.PREFS_KEY_CHEAT_TEXT, "Cheat text goes here."));
    }
}
