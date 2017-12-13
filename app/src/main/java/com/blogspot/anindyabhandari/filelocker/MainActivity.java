package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void goToKeychain(View view)
    {
        Intent intent = new Intent(this, ReadOrCreate.class);
        startActivity(intent);
    }
    public void goToRequest(View view)
    {
        Intent intent = new Intent(this, RequestSecretForKeyValue.class);
        startActivity(intent);
    }
}
