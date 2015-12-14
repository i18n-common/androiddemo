package com.aspire.broadcastwrite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BROADCAST_WRITE_TO_FILE = "com.aspire.write_to_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button mSendBroadcast = (Button) findViewById(R.id.btn_send_broadcast);
        mSendBroadcast.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_broadcast) {
            Log.d(TAG, "==>sendBroadcastWrite");
            sendBroadcastWrite();
        }
    }

    private void sendBroadcastWrite() {
        Intent bcIntent = new Intent(BROADCAST_WRITE_TO_FILE);
        sendBroadcast(bcIntent);
    }
}
