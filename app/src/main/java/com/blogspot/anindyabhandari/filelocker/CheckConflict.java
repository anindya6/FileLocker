package com.blogspot.anindyabhandari.filelocker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class CheckConflict extends GoogleDriveTools {
    final int intentcode = 5649;
    String dTAG = "CheckConflict::";
    int ver_existing = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_conflict);
        Intent checker = getIntent();
        ver_existing = checker.getIntExtra("version",0);
        Intent qFile = new Intent(this, QueryFilesActivity.class);
        startActivityForResult(qFile, intentcode);
    }

    @Override
    protected void onDriveClientReady() {
    }

    protected void display(int mode, DriveId result) {
        if (mode == 1) {
            retrieveContents(result.asDriveFile());
        }
        else
        {
            Intent intent = new Intent(this, SyncAgent.class);
            startActivity(intent);
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == intentcode) {
            if (resultCode == RESULT_OK) {
                int found = data.getIntExtra("located", 0);
                DriveId dr_id;
                if (found==1)
                    dr_id = DriveId.decodeFromString(data.getStringExtra("data"));
                else
                    dr_id = null;
                display(found, dr_id);
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void retrieveContents(final DriveFile file) {
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        InputStream fis = contents.getInputStream();
                        Log.e(dTAG, "Here 3?");
                        byte[] buffer2 = new byte[1024]; // fis.;
                        int len = fis.read(buffer2);
                        Log.e(dTAG, buffer2.toString());
                        fis.close();
                        byte[] iv = new byte[12];
                        byte[] salt = new byte[32];
                        byte[] alias_b = new byte[36];
                        byte[] versionNo = new byte[4];
                        byte[] encrypted = new byte[len - 84];
                        int i;
                        for (i = 0; i < len; i++) {
                            if (i < 12)
                                iv[i] = buffer2[i];
                            else if (i < 44)
                                salt[i - 12] = buffer2[i];
                            else if (i < 80)
                                alias_b[i - 44] = buffer2[i];
                            else if (i < 84)
                                versionNo[i - 80] = buffer2[i];
                            else
                                encrypted[i - 84] = buffer2[i];
                        }
                        String alias = new String(alias_b, "UTF-8");
                        Log.e(dTAG, alias);
                        String result;
                        int mul = 1;
                        int ver = 0;
                        for (int j = 3; j >= 0; j--) {
                            ver += mul * (int) versionNo[j];
                            mul *= 128;
                        }
                        //int ver_existing = getCurrentVerNo("myfile1");
                        if(ver<=ver_existing)
                        {
                            Intent _result = new Intent();
                            _result.putExtra("ConflictStatus",0);
                            setResult(RESULT_OK, _result);
                            finish();

                        }
                        else
                        {
                            Intent _result = new Intent();
                            _result.putExtra("ConflictStatus",1);
                            setResult(RESULT_OK, _result);
                            finish();
                        }
                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(dTAG, "Unable to read contents", e);
                        showMessage("Read failed");
                        finish();
                    }
                });
    }
}
