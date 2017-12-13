package com.blogspot.anindyabhandari.filelocker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Activity to illustrate how to retrieve and read file contents.
 */
public class getContents extends GoogleDriveTools {
    private static final String TAG = "RetrieveContents";

    /**
     * Text view for file contents
     */
    private TextView mFileContents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_contents);
    }

    @Override
    protected void onDriveClientReady() {
        pickFile("myfile1")
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                retrieveContents(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                        showMessage("File not selected");
                        finish();
                    }
                });
    }
    private void retrieveContents(DriveFile file) {
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        InputStream fis = contents.getInputStream();
                        Log.e(TAG,"Here 3?");
                        byte[] buffer2 = new byte[1024]; // fis.;
                        int len = fis.read(buffer2);
                        Log.e(TAG,buffer2.toString());
                        fis.close();
                        byte [] iv = new byte [12];
                        byte [] salt = new byte[32];
                        byte [] alias_b = new byte [36];
                        byte [] versionNo = new byte [4];
                        byte [] encrypted = new byte [len - 84];
                        int i;
                        for(i=0;i<len;i++)
                        {
                            if(i<12)
                                iv[i]=buffer2[i];
                            else if (i<44)
                                salt[i-12]=buffer2[i];
                            else if (i<80)
                                alias_b[i-44]=buffer2[i];
                            else if (i<84)
                                versionNo[i-80]=buffer2[i];
                            else
                                encrypted[i-84]=buffer2[i];
                        }
                        String alias = new String (alias_b, "UTF-8");
                        Log.e(TAG,alias);
                        String result;
                        int mul=1;
                        int ver=0;
                        for(int j=3;j>=0;j--) {
                            ver += mul * (int) versionNo[j];
                            mul *= 128;
                        }
                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to read contents", e);
                        showMessage("Read failed");
                        finish();
                    }
                });
    }
}
