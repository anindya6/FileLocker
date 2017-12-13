package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ShowSecretForKey extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_secret_for_key);
        Intent intent = getIntent();
        TextView textView1 = (TextView) findViewById(R.id.textView24);
        TextView textView2 = (TextView) findViewById(R.id.textView26);
        textView1.setText(intent.getStringExtra("NetworkName"));
        textView2.setText(intent.getStringExtra("Password"));
    }

    public void onDone(View view) {
        finish();
    }
}
