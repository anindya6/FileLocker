package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


import static com.blogspot.anindyabhandari.filelocker.Keychain.decryptFile;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getNewKeyWithAlias;
import static com.blogspot.anindyabhandari.filelocker.SecretKeyStore.getSecretKeyWithAlias;

public class CredRetrieval extends AppCompatActivity {
    final int intentcode = 1912;
    String networkName="DoesNotExist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cred_retrieval);
        Intent intent = getIntent();
        networkName = intent.getStringExtra("NetworkName");
        getTheCred(0,"");
    }
    public void getTheCred(int ix, String password)
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
            String alias = new String (alias_b, "UTF-8");
            String result;
            SecretKey sk = KeyGenerator.getInstance("AES").generateKey(); //just for initialization
            int mul=1;
            int ver=0;
            for(int j=3;j>=0;j--) {
                ver += mul * (int) versionNo[j];
                mul *= 128;
            }
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
                Intent _result = new Intent();
                result = decryptFile(encrypted, sk, iv);
                if (JSONHandling.stringHasCred(result,networkName))
                {
                    String pass = JSONHandling.getPasswordFromStringCred(result, networkName);
                    _result.putExtra("Status", 0);//Status 0 means the operation succeeded
                    _result.putExtra("Password", pass);
                }
                else
                {
                    _result.putExtra("Status", 1);//Status 1 means the networkName did not exist in the credential file
                }
                setResult(RESULT_OK,_result);
                finish();
            }
        }
        catch(Exception e)
        {
            Log.e("CredRetrieval::","File Read Error: "+e.getMessage());
            Intent _result = new Intent();
            _result.putExtra("Status", 2);//Status 2 implies there was an error of some sort
            setResult(RESULT_OK,_result);
            finish();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == intentcode) {
            if(resultCode == RESULT_OK){
                String password=data.getStringExtra("password");
                getTheCred(1,password);
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