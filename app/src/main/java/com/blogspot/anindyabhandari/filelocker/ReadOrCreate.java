package com.blogspot.anindyabhandari.filelocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.google.android.gms.drive.Drive.getDriveClient;
import static com.google.android.gms.drive.Drive.getDriveResourceClient;

public class ReadOrCreate extends AppCompatActivity {
    static int mode=0;
    final int intentcode = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_or_create);
        Intent syncFile = new Intent(this, SyncMode.class);
        startActivityForResult(syncFile,intentcode);
    }
    public void whatToDisplay() {
        Button button = (Button)findViewById(R.id.button14);
        try {
            FileInputStream fis = openFileInput("myfile1");
            fis.close();
            button.setText("Read Credentials File");
            mode = 1;
        }
        catch(Exception e)
        {
            button.setText("Create Credentials File");
            mode = 0;
        }
    }
    public void onSubmit(View view)
    {
        if(mode ==1)
        {
            Intent intent = new Intent(this,DisplayFile.class);
            intent.putExtra("title","Displaying Credentials File..");
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this,CreateFile.class);
            intent.putExtra("title","Create File in Progress..");
            startActivity(intent);
        }
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == intentcode) {
            whatToDisplay();
        }
    }
}
