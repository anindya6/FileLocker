package com.blogspot.anindyabhandari.filelocker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileOutputStream;

import javax.crypto.SecretKey;


import static com.blogspot.anindyabhandari.filelocker.Keychain.returnEncryptedBlobWithIV;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getNewKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getSalt;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getStringUUID;

public class CreateFile extends AppCompatActivity {
    final static int req_code = 643;
    static String password="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(title==null)
            title = "Creating Credentials File..";
        TextView textView = (TextView)findViewById(R.id.textView19);
        textView.setText(title);
        getPassword(0);
    }
    public void getPassword(int mode)
    {
        Intent newPass = new Intent(this,NewPasswordDialog.class);
        if(mode==0)
            newPass.putExtra("title","Create new password: ");
        if(mode==1)
            newPass.putExtra("title","Password mismatch, Please re-enter: ");
        startActivityForResult(newPass,req_code);
    }
    public void onSubmit(View view)
    {
        EditText editText2 = (EditText) findViewById(R.id.editText8);
        EditText editText = (EditText) findViewById(R.id.editText6);
        String netName = editText2.getText().toString();
        String passData = editText.getText().toString();
        String fileData = JSONHandling.createStringCredObject(netName,passData);
        byte [] iv={1};
        String st;
        byte [][] val;
        try {
            byte[] salt = getSalt();
            String alias = getStringUUID();
            SecretKey sk = getNewKeyWithAlias(alias, password, salt);
            val = returnEncryptedBlobWithIV(fileData, sk);
            iv = val[1];
            byte[] aliasBytes = alias.getBytes("UTF-8");
            byte [] versionNo = new byte [4];
            versionNo[0]=versionNo[1]=versionNo[2]=(byte)0;
            versionNo[3]=(byte)1;
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
        finish();
    }
    public void onCancel(View view)
    {
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == req_code) {
            if(resultCode == RESULT_OK){
                int fail = data.getIntExtra("password_mismatch",1);
                if(fail==0)
                {
                    password=data.getStringExtra("password");
                    Log.e("Returned Value: ",password);
                }
                else
                {
                    getPassword(fail);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}
