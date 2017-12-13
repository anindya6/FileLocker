package com.blogspot.anindyabhandari.filelocker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static com.blogspot.anindyabhandari.filelocker.Keychain.decryptFile;
import static com.blogspot.anindyabhandari.filelocker.Keychain.returnEncryptedBlobWithIV;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.deleteSecretKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getNewKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getSalt;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getSecretKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getStringUUID;

public class DisplayFile extends AppCompatActivity {
    static String pass = "";
    final static int intentcode = 412;
    static String save_alias="";
    static byte [] save_salt;
    static int save_ver=1;
    static String save_contents = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(title==null)
            title="Displaying Credentials File: ";
        TextView textView = (TextView) findViewById(R.id.textView13);
        textView.setText(title);
        showTheFile(0,"");
    }
    public void showTheFile(int ix, String password)
    {
        String filename = "myfile1";
        try {
            FileInputStream fis =  openFileInput(filename);
            byte[] buffer2 =   new byte[(int) fis.getChannel().size()];
            fis.read(buffer2);
            fis.close();
            byte [] iv = new byte [12];
            byte [] salt = new byte[32];
            byte [] alias_b = new byte [36];
            byte [] versionNo = new byte [4];
            byte [] encrypted = new byte [buffer2.length - 84];
            int i;
            for(i=0;i<buffer2.length;i++)
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
            Log.e("encrypted read",encrypted.toString());
            String alias = new String (alias_b, "UTF-8");
            save_salt = salt;
            save_alias=alias.toString();
            String result;
            SecretKey sk = KeyGenerator.getInstance("AES").generateKey(); //just for initialization
            int mul=1;
            int ver=0;
            for(int j=3;j>=0;j--) {
                ver += mul * (int) versionNo[j];
                mul *= 128;
            }
            save_ver = ver;
            if(ix==1)
                sk = getNewKeyWithAlias(alias,password,salt);
            else
                sk = getSecretKeyWithAlias(alias);
            if(sk==null)
            {
                Intent getPass = new Intent(this,GetPasswordDialog.class);
                startActivityForResult(getPass,intentcode);
            }
            else
            {
                result = decryptFile(encrypted, sk, iv);
                save_contents = result;
                TextView textView = (TextView) findViewById(R.id.textView12);
                textView.setText(result+"\nVersion Number is: "+ver);
            }
        }
        catch(Exception e)
        {
            TextView textView=(TextView)findViewById(R.id.textView12);
            textView.setText("Error occured, it is: "+e);
        }
    }
    public void onDeleteCred(View view)
    {
        TextView textView=(TextView)findViewById(R.id.textView13);
        File dir = getFilesDir();
        File file = new File(dir, "myfile1");
        boolean deleted = file.delete();
        if(deleted==true) {
            showMessage("Credentials File Deleted");
            finish();
        }
        else {
            showMessage("File could not be deleted.");
            finish();
        }
    }
    public void onDeleteKey(View view)
    {
        deleteSecretKeyWithAlias(save_alias);
        showMessage("Key Deleted");
        finish();
    }
    public void onUpdate(View view)
    {
        Intent update = new Intent (this, UpdateFile.class);
        update.putExtra("alias",save_alias);
        update.putExtra("salt",save_salt);
        update.putExtra("version",save_ver);
        update.putExtra("content",save_contents);
        update.putExtra("title","Update file to : ");
        startActivity(update);
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == intentcode) {
            if(resultCode == RESULT_OK){
                String password=data.getStringExtra("password");
                Log.e("Returned Value: ",password);
                showTheFile(1,password);
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

