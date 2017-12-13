package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.widget.DataBufferAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.InputStream;

import com.blogspot.anindyabhandari.filelocker.R;

public class QueryFilesActivity extends GoogleDriveTools {
    private static final String TAG = "QueryFiles";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_query_files);
    }

    @Override
    protected void onDriveClientReady() {
        listFiles();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    private void listFiles() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "myfile1"))
                .build();
        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                for(Metadata m: metadataBuffer)
                                {
                                    if(m.isInAppFolder() && !m.isTrashed()) {
                                        Intent _result = new Intent();
                                        _result.putExtra("located", 1);
                                        _result.putExtra("data",m.getDriveId().encodeToString());
                                        setResult(RESULT_OK, _result);
                                        metadataBuffer.release();
                                        finish();
                                    }
                                }
                                Intent _result = new Intent();
                                _result.putExtra("located",0);
                                _result.putExtra("data","");
                                setResult(RESULT_OK,_result);
                                metadataBuffer.release();
                                finish();
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving files", e);
                        showMessage("Query Failed");
                        finish();
                    }
                });
    }
}
