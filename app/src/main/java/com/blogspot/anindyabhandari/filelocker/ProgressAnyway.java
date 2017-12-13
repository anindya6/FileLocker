package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class ProgressAnyway extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_anyway);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if (title == null)
            title = "Progress Anyway?";
        TextView textView = (TextView) findViewById(R.id.textView27);
        textView.setText(title);
    }
    protected void onYes(View view)
    {
        setResult(RESULT_OK);
        finish();
    }
    protected void onNo(View view)
    {
        finish();
    }
}
