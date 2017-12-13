package com.blogspot.anindyabhandari.filelocker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import javax.crypto.SecretKey;

import static com.blogspot.anindyabhandari.filelocker.Keychain.returnEncryptedBlobWithIV;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getNewKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getSalt;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getSecretKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getStringUUID;

public class UpdateFile extends AppCompatActivity {
    String alias="";
    byte [] salt;
    int version=2;
    String content = "";
    final int intentcode = 1771;
    final int intentcode2 = 1902;
    final int intentcode3 = 7881;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_file);
        TextView textView = (TextView) findViewById(R.id.textView21);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(title == null)
            title = "Please enter the credentials to update";
        textView.setText(title);
        alias = intent.getStringExtra("alias");
        salt = intent.getByteArrayExtra("salt");
        version = intent.getIntExtra("version",2);
        content = intent.getStringExtra("content");
        //EditText editText = (EditText)findViewById(R.id.editText7);
        //editText.setText(content);
    }
    public void onSubmit(View View)
    {
        Intent conflict = new Intent(this, CheckConflict.class);
        conflict.putExtra("version",version);
        startActivityForResult(conflict,intentcode);
    }
    public void writeToFile()
    {
        EditText editText = (EditText) findViewById(R.id.editText7);
        String passData = editText.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.editText9);
        String netName = editText2.getText().toString();
        String fileData = JSONHandling.addCredGetString(content,netName,passData);
        Log.e("update::",fileData);
        byte [] iv={1};
        String st;
        byte [][] val;
        try {
            SecretKey sk = getSecretKeyWithAlias(alias);
            val = returnEncryptedBlobWithIV(fileData, sk);
            iv = val[1];
            byte[] aliasBytes = alias.getBytes("UTF-8");
            byte [] versionNo = getUpdatedVersionNo(version);
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput("myfile1", Context.MODE_PRIVATE);
                outputStream.write(iv);
                outputStream.write(salt);
                outputStream.write(aliasBytes);
                outputStream.write(versionNo);
                outputStream.write(val[0]);
                outputStream.close();
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        catch(Exception e)
        {
            Log.e("ErrorCreateFileSubmit",e.getMessage());
        }
        showMessage("File Updated Locally!");
        Intent intent = new Intent (this, SyncMode.class);
        intent.putExtra("overwrite",1);
        startActivity(intent);
        finish();
    }
    public byte [] getUpdatedVersionNo (int ver)
    {
        byte [] v = new byte [4];
        int j = 3;
        ver = ver+1;
        while(ver!=0)
        {
            v[j--] = (byte)(ver%128);
            ver/=128;
            if (j==-1)//error case
                break;
        }
        return v;
    }
    public void deleteCred()
    {
        File dir = getFilesDir();
        File file = new File(dir, "myfile1");
        boolean deleted = file.delete();
        if(deleted==true) {
            Log.e("update::","Successful deletion");
        }
        else {
            Log.e("update::","Delete Failed");
        }
    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == intentcode) {
            if(resultCode == RESULT_OK){
                int conf_status=data.getIntExtra("ConflictStatus",0);
                if(conf_status==0) {
                    EditText editText2 = (EditText) findViewById(R.id.editText9);
                    String netName = editText2.getText().toString();
                    if(JSONHandling.stringHasCred(content,netName))
                    {
                        Intent intent = new Intent(this, ProgressAnyway.class);
                        intent.putExtra("title","Network Name exists in the Keychain. Update password for network?");
                        startActivityForResult(intent,intentcode3);
                    }
                    else
                    {
                        writeToFile();
                    }
                }
                else {
                    Intent intent = new Intent(this, ProgressAnyway.class);
                    intent.putExtra("title","Conflict Detected. Newer Version Exists. Overwrite anyway?");
                    startActivityForResult(intent,intentcode2);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                showMessage("Cancelled.");
                finish();
            }
        }
        else if (requestCode == intentcode2){
            if (resultCode == RESULT_OK){
                EditText editText2 = (EditText) findViewById(R.id.editText9);
                String netName = editText2.getText().toString();
                if(JSONHandling.stringHasCred(content,netName))
                {
                    Intent intent = new Intent(this, ProgressAnyway.class);
                    intent.putExtra("title","Network Name exists in the Keychain. Update password for network?");
                    startActivityForResult(intent,intentcode3);
                }
                else
                {
                    writeToFile();
                }
            }
            else{
                showMessage("Cancelled.");
                finish();
            }
        }
        else if(requestCode == intentcode3)
        {
            if(resultCode == RESULT_OK){
                writeToFile();
            }
            else {
                showMessage("Cancelled.");
                finish();
            }
        }
    }
}